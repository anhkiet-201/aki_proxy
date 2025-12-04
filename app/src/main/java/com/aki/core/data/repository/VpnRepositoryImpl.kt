package com.aki.core.data.repository

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.aki.app.receiver.VpnStateReceiver
import com.aki.app.services.ProxyServices
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.model.VpnState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VpnRepository {

    override val vpnState: Flow<VpnState> = callbackFlow {
        val receiver = VpnStateReceiver().apply {
            onStateUpdate = { state ->
                trySend(state)
            }
        }
        val intentFilter = IntentFilter(VpnStateReceiver.ACTION_STATE_UPDATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            ContextCompat.registerReceiver(
                context,
                receiver,
                intentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        // Emit the initial state
        trySend(VpnState.Disconnected)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    override suspend fun startVpn(config: VpnConfig) {
        val intent = Intent(context, ProxyServices::class.java).apply {
            action = ProxyServices.ACTION_CONNECT
            putExtra(ProxyServices.EXTRA_CONFIG, config)
        }
        context.startService(intent)
    }

    override suspend fun stopVpn() {
        val intent = Intent(context, ProxyServices::class.java).apply {
            action = ProxyServices.ACTION_DISCONNECT
        }
        context.startService(intent)
    }
}
