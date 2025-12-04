//package com.aki.app.receiver
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import com.aki.core.data.repository.VpnRepository
//import com.aki.core.domain.model.VpnState
//import com.aki.core.domain.usecase.GetSelectedConfigUseCase
//import dagger.hilt.EntryPoint
//import dagger.hilt.InstallIn
//import dagger.hilt.android.EntryPointAccessors
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//
//class BootReceiver : BroadcastReceiver() {
//
//    @EntryPoint
//    @InstallIn(SingletonComponent::class)
//    interface BootReceiverEntryPoint {
//        fun vpnRepository(): VpnRepository
//        fun getSelectedConfigUseCase(): GetSelectedConfigUseCase
//    }
//
//    companion object {
//        const val TAG = "BootReceiver"
//    }
//
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
//            return
//        }
//
//        Log.i(TAG, "Device boot completed. Checking for auto-connect...")
//
//        val pendingResult = goAsync()
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val entryPoint = EntryPointAccessors.fromApplication(
//                    context.applicationContext,
//                    BootReceiverEntryPoint::class.java
//                )
//                val vpnRepository = entryPoint.vpnRepository()
//                val getSelectedConfigUseCase = entryPoint.getSelectedConfigUseCase()
//
//                // Lấy cấu hình đã được chọn gần đây nhất
//                val selectedConfig = getSelectedConfigUseCase.execute(Unit).first()
//
//                if (selectedConfig != null && selectedConfig.status == "CONNECTED") {
//                    Log.i(TAG, "Found saved config: ${selectedConfig.config.host}. Starting VPN.")
//                    vpnRepository.startVpn(selectedConfig.config)
//                } else {
//                    Log.i(TAG, "No saved config found. Skipping auto-connect.")
//                }
//
//            } catch (e: Exception) {
//                Log.e(TAG, "Error during auto-connect on boot.", e)
//            } finally {
//                pendingResult.finish()
//            }
//        }
//    }
//}
