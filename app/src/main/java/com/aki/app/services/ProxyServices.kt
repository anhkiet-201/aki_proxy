
package com.aki.app.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.IpPrefix
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aki.app.receiver.VpnStateReceiver
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.model.VpnState
import com.aki.core.domain.usecase.GetSelectedConfigUseCase
import com.aki.proxy.Tun2SocksJni
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetAddress
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class ProxyServices : VpnService() {

    private val TAG = "ProxyServices"

    @Inject
    lateinit var getSelectedConfigUseCase: GetSelectedConfigUseCase
    private var vpnInterface: ParcelFileDescriptor? = null
    private var vpnThread: Thread? = null
    var config: VpnConfig? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_CONNECT = "com.aki.proxy.CONNECT"
        const val ACTION_DISCONNECT = "com.aki.proxy.DISCONNECT"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "vpn_channel"
        const val EXTRA_CONFIG = "CONFIG"
        const val BYPASS_DNS_IP = "1.1.1.1"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        Log.i(TAG, "onStartCommand called. Action: $action")

        when (action) {
            ACTION_CONNECT -> {
                config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_CONFIG, VpnConfig::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_CONFIG)
                }

                if (config != null) {
                    startVpn(config!!)
                } else {
                    broadcastState("ERROR", VpnConfig("unknown", 0), "Invalid VPN configuration.")
                    stopSelf()
                }
                return START_STICKY
            }
            ACTION_DISCONNECT -> {
                stopVpnSafe()
                return START_NOT_STICKY
            }
            SERVICE_INTERFACE -> {
                serviceScope.launch {
                    config = getSelectedConfigUseCase.execute(Unit).first()?.config
                    if (config != null) {
                        Log.i(TAG, "System restart detected. Auto-connecting with: ${config!!.host}")
                        startVpn(config!!)
                    } else {
                        Log.w(TAG, "System restart detected, but no config found. Stopping.")
                        stopSelf()
                    }
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (config != null) {
            broadcastState("DISCONNECTED", config!!)
        }
        super.onDestroy()
    }

    private fun startVpn(config: VpnConfig) {
        broadcastState("CONNECTING", config)
        val notification = createNotification()
        try {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground", e)
            broadcastState("ERROR", config, "Failed to start foreground service.")
            return
        }

        vpnThread = thread(name = "Tun2Socks-Thread", start = true) {
            try {
                val pd = configureVpnInterface(config.host)
                val fd = pd?.fd ?: throw IOException("Failed to create VPN Interface (Permissions may be required)")

                broadcastState("CONNECTED", config)
                Log.i(TAG, "VPN established. FD=$fd. Starting Native Engine...")

                val serverAddress = "${config.host}:${config.port}"

                Tun2SocksJni.startTun2Socks(
                    fd,
                    1500, // MTU
                    serverAddress,
                    config.user,
                    config.pass
                )
                
                Log.i(TAG, "Native engine stopped normally.")
                broadcastState("DISCONNECTED", config)

            } catch (e: Exception) {
                Log.e(TAG, "Error in VPN thread", e)
                broadcastState("ERROR", config, e.message ?: "Unknown VPN error")
            } finally {
                closeResources()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
    }

    private fun stopVpnSafe() {
        Log.i(TAG, "Stopping VPN...")
        try {
            Tun2SocksJni.stopTun2Socks()
        } catch (e: Exception) {
            Log.e(TAG, "Error signaling stop", e)
        }

        try {
            vpnThread?.interrupt() // Interrupt the thread
            vpnThread?.join(2000) // Wait for it to finish
        } catch (e: InterruptedException) {
            Log.e(TAG, "Interrupted while waiting for VPN thread")
        }

        closeResources()
        stopSelf() // Stop the service itself
    }

    private fun closeResources() {
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing FD", e)
        }
        vpnInterface = null
    }
    
    private fun broadcastState(vpnState: String, config: VpnConfig, errorMessage: String? = null) {
        val intent = Intent(VpnStateReceiver.ACTION_STATE_UPDATE).apply {
            setPackage(packageName)
            putExtra(VpnStateReceiver.EXTRA_STATUS, vpnState)
            putExtra(VpnStateReceiver.EXTRA_CONFIG, config)
            errorMessage?.let { putExtra(VpnStateReceiver.EXTRA_ERROR_MESSAGE, it) }
        }
        sendBroadcast(intent)
    }

    // ... (configureVpnInterface and other helper methods remain the same) ...
    private fun configureVpnInterface(proxyHost: String): ParcelFileDescriptor? {
        if (vpnInterface != null) return vpnInterface

        val builder = Builder()
        builder.setSession("Socks5 VPN")
        builder.setMtu(1500)
        builder.addAddress("10.0.0.2", 32)

        var proxyIp = ""
        try {
            val inetAddress = InetAddress.getByName(proxyHost)
            proxyIp = inetAddress.hostAddress ?: ""
            Log.i(TAG, "Resolved Proxy Host '$proxyHost' to IP: $proxyIp")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resolve proxy host: $proxyHost. Loops may occur!", e)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                builder.addRoute("0.0.0.0", 0)
                if (proxyIp.isNotEmpty()) {
                    builder.excludeRoute(IpPrefix(InetAddress.getByName(proxyIp), 32))
                    Log.i(TAG, "Excluded Proxy IP ($proxyIp) from VPN.")
                }
                builder.excludeRoute(IpPrefix(InetAddress.getByName(BYPASS_DNS_IP), 32))
                Log.i(TAG, "Excluded DNS IP ($BYPASS_DNS_IP) from VPN.")

            } catch (e: Exception) {
                Log.e(TAG, "Android 13 excludeRoute failed", e)
                fallbackRouteConfig(builder, proxyIp)
            }
        } else {
            fallbackRouteConfig(builder, proxyIp)
        }

        try {
            builder.addDisallowedApplication(packageName)
        } catch (e: Exception) {
            Log.e(TAG, "Error excluding package", e)
        }

        try {
            builder.setBlocking(true)
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting blocking", e)
        }

        vpnInterface = builder.establish()
        return vpnInterface
    }

    private fun fallbackRouteConfig(builder: Builder, proxyIp: String) {
        val ipToExclude = if (proxyIp.isNotEmpty()) proxyIp else BYPASS_DNS_IP

        Log.i(TAG, "Fallback Routing: Splitting routes to exclude $ipToExclude")
        try {
            addRoutesExcluding(builder, ipToExclude)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add split routes. Adding default 0.0.0.0/0 (Risk of Loop!)", e)
            builder.addRoute("0.0.0.0", 0)
        }
    }

    private fun addRoutesExcluding(builder: Builder, excludedIpStr: String) {
        val excludedIp = parseIpToLong(excludedIpStr)
        var currentPrefix: Long = 0
        var prefixLength = 0

        for (i in 31 downTo 0) {
            val bit = (excludedIp shr i) and 1
            if (bit == 0L) {
                val routeIp = currentPrefix or (1L shl i)
                builder.addRoute(longToIpString(routeIp), prefixLength + 1)
            } else {
                val routeIp = currentPrefix
                builder.addRoute(longToIpString(routeIp), prefixLength + 1)
                currentPrefix = currentPrefix or (1L shl i)
            }
            prefixLength++
        }
    }

    private fun parseIpToLong(ipAddress: String): Long {
        val parts = ipAddress.split(".")
        var result: Long = 0
        for (part in parts) {
            result = result shl 8
            result = result or part.toLong()
        }
        return result
    }

    private fun longToIpString(ip: Long): String {
        return "${(ip shr 24) and 0xFF}.${(ip shr 16) and 0xFF}.${(ip shr 8) and 0xFF}.${ip and 0xFF}"
    }

    private fun createNotification(): Notification {
        val channelId = CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(NotificationChannel(channelId, "VPN", NotificationManager.IMPORTANCE_LOW))
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, Intent(this, ProxyServices::class.java).apply { action = ACTION_DISCONNECT },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("SOCKS5 VPN")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_lock_lock)
            .addAction(R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()
    }
}
