package com.aki.app.viewmodel

import android.app.Application
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.viewModelScope
import com.aki.app.base.BaseViewModel
import com.aki.core.data.repository.VpnRepository
import com.aki.core.domain.model.SelectedVpnConfig
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.model.VpnState
import com.aki.core.domain.usecase.GetSelectedConfigUseCase
import com.aki.core.domain.usecase.SaveSelectedConfigUseCase
import com.aki.core.domain.usecase.StartVpnUseCase
import com.aki.core.domain.usecase.StopVpnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
    vpnRepository: VpnRepository,
    getSelectedConfigUseCase: GetSelectedConfigUseCase,
    private val startVpnUseCase: StartVpnUseCase,
    private val stopVpnUseCase: StopVpnUseCase,
) : BaseViewModel() {

    val vpnState: StateFlow<VpnState> = vpnRepository.vpnState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VpnState.Disconnected)

    val selectedConfig: StateFlow<SelectedVpnConfig?> = flow {
        emitAll(getSelectedConfigUseCase.execute(Unit))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _permissionEvent = Channel<Intent>()
    val permissionEvent = _permissionEvent.receiveAsFlow()

    fun onConnectClick() {
        viewModelScope.launch {
            val config = selectedConfig.value?.config ?: return@launch // Chỉ kết nối nếu có config

            val prepareIntent = VpnService.prepare(application)
            if (prepareIntent != null) {
                _permissionEvent.send(prepareIntent)
            } else {
                startVpnUseCase.execute(config)
            }
        }
    }

    fun proceedToConnect() {
        viewModelScope.launch {
            val config = selectedConfig.value?.config ?: return@launch
            startVpnUseCase.execute(config)
        }
    }

    fun onDisconnectClick() {
        viewModelScope.launch {
            stopVpnUseCase.execute(Unit)
        }
    }

//    fun onServerSelected(config: VpnConfig) {
//        viewModelScope.launch {
//            saveSelectedConfigUseCase.execute(config)
//        }
//    }
}
