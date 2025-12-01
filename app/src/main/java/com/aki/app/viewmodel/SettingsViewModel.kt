package com.aki.app.viewmodel

import com.aki.app.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : BaseViewModel() {

    private val _autoConnect = MutableStateFlow(true)
    val autoConnect = _autoConnect.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    fun onAutoConnectChanged(enabled: Boolean) {
        _autoConnect.value = enabled
        // TODO: Save this preference to VpnStorage
    }

    fun onNotificationsChanged(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        // TODO: Save this preference to VpnStorage
    }
}
