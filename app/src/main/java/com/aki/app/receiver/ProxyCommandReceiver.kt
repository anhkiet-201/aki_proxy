package com.aki.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aki.core.data.repository.VpnRepository
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.usecase.SaveConfigUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProxyCommandReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProxyCommandReceiverEntryPoint {
        fun vpnRepository(): VpnRepository
        fun saveConfigUseCase(): SaveConfigUseCase // Thêm use case để lưu vào danh sách
    }

    companion object {
        const val TAG = "ProxyCommandReceiver"
        const val ACTION_CONNECT = "com.aki.proxy.CONNECT"
        const val ACTION_DISCONNECT = "com.aki.proxy.DISCONNECT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, ">>> Command received! Action: ${intent.action}")

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    ProxyCommandReceiverEntryPoint::class.java
                )
                val vpnRepository = entryPoint.vpnRepository()
                val saveConfigUseCase = entryPoint.saveConfigUseCase()

                when (intent.action) {
                    ACTION_CONNECT -> {
                        val host = intent.getStringExtra("HOST")
                        val port = intent.getStringExtra("PORT")

                        if (host.isNullOrBlank() || port.isNullOrBlank()) {
                            Log.e(TAG, "CONNECT command received without required HOST or PORT extras.")
                            return@launch
                        }

                        try {
                            val config = VpnConfig(
                                host = host,
                                port = port.toInt(),
                                user = intent.getStringExtra("USER"),
                                pass = intent.getStringExtra("PASS")
                            )
                            Log.i(TAG, "Received CONNECT command. Saving and starting VPN with config: $config")

                            // 1. Thêm cấu hình này vào danh sách chung
                            saveConfigUseCase.execute(config)
                            
                            // 3. Bắt đầu VPN
                            vpnRepository.startVpn(config)

                        } catch (e: NumberFormatException) {
                            Log.e(TAG, "Invalid PORT format. Must be an integer.", e)
                        }
                    }
                    ACTION_DISCONNECT -> {
                        Log.i(TAG, "Received DISCONNECT command. Stopping VPN.")
                        vpnRepository.stopVpn()
                    }
                }
            } finally {
                // Luôn gọi finish() để báo cho hệ thống rằng công việc đã hoàn tất.
                pendingResult.finish()
            }
        }
    }
}
