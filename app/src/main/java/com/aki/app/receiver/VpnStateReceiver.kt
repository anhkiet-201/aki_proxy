package com.aki.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.aki.core.domain.model.SelectedVpnConfig
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.model.VpnState
import com.aki.core.domain.usecase.SaveSelectedConfigUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VpnStateReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface VpnStateReceiverEntryPoint {
        fun saveSelectedConfigUseCase(): SaveSelectedConfigUseCase
    }

    var onStateUpdate: ((VpnState) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_STATE_UPDATE) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                VpnStateReceiverEntryPoint::class.java
            )
            val saveSelectedConfigUseCase = entryPoint.saveSelectedConfigUseCase()

            val status = intent.getStringExtra(EXTRA_STATUS) ?: "DISCONNECTED"
            val config: VpnConfig? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_CONFIG, VpnConfig::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_CONFIG)
            }
            saveSelectedConfigUseCase.execute(
                SelectedVpnConfig(
                    config ?: VpnConfig("unknown", 0),
                    status
                )
            )
            val vpnState = when(status) {
                "CONNECTED" -> VpnState.Connected
                "CONNECTING" -> VpnState.Connecting
                else -> VpnState.Disconnected
            }
            onStateUpdate?.invoke(
                vpnState
            )
            pendingResult.finish()
        }
    }

    companion object {
        const val ACTION_STATE_UPDATE = "com.aki.proxy.STATE_UPDATE"
        const val EXTRA_STATUS = "STATUS"
        const val EXTRA_CONFIG = "CONFIG"
        const val EXTRA_ERROR_MESSAGE = "ERROR_MESSAGE"
    }
}
