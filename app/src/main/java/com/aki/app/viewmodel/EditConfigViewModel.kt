package com.aki.app.viewmodel

import androidx.lifecycle.viewModelScope
import com.aki.app.base.BaseViewModel
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.usecase.SaveConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditConfigViewModel @Inject constructor(
    private val saveConfigUseCase: SaveConfigUseCase
) : BaseViewModel() {

    private val _host = MutableStateFlow("")
    val host = _host.asStateFlow()

    private val _port = MutableStateFlow("")
    val port = _port.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onHostChange(value: String) {
        _host.value = value
    }

    fun onPortChange(value: String) {
        _port.value = value
    }

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun saveConfig() {
        viewModelScope.launch {
            val config = VpnConfig(
                host = _host.value.trim(),
                port = _port.value.trim().toIntOrNull() ?: 0,
                user = _username.value.trim().takeIf { it.isNotEmpty() },
                pass = _password.value.trim().takeIf { it.isNotEmpty() }
            )
            // TODO: Add validation for host and port
            saveConfigUseCase.execute(config)
        }
    }
}
