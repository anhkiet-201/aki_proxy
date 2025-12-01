package com.aki.app.viewmodel

import androidx.lifecycle.viewModelScope
import com.aki.app.base.BaseViewModel
import com.aki.core.domain.model.SelectedVpnConfig
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.usecase.GetConfigsUseCase
import com.aki.core.domain.usecase.RemoveConfigUseCase
import com.aki.core.domain.usecase.SaveSelectedConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data class to represent a server in the list
data class ServerDisplayInfo(
    val config: VpnConfig,
    val name: String,
    val location: String,
    val flagUrl: String, // In a real app, this might be a resource ID
    val signalStrength: Int // e.g., 1-3
)

@HiltViewModel
class RecentsConfigsViewModel @Inject constructor(
    getConfigsUseCase: GetConfigsUseCase,
    private val saveSelectedConfigUseCase: SaveSelectedConfigUseCase,
    private val removeConfigUseCase: RemoveConfigUseCase
) : BaseViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val configs: Flow<List<VpnConfig>> = flow {
        emitAll(getConfigsUseCase.execute(Unit))
    }

    val filteredServers: StateFlow<List<ServerDisplayInfo>> = _searchQuery
        .combine(configs) { query, configs ->
            val serverInfos = configs.map { config ->
                ServerDisplayInfo(
                    config = config,
                    name = config.host, // Use host as name for now
                    location = "Unknown",
                    flagUrl = "",
                    signalStrength = 3
                )
            }

            if (query.isBlank()) {
                serverInfos
            } else {
                serverInfos.filter {
                    it.name.contains(query, ignoreCase = true) || it.location.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onServerSelected(config: SelectedVpnConfig) {
        viewModelScope.launch {
            saveSelectedConfigUseCase.execute(config)
        }
    }

    fun onServerSwiped(config: VpnConfig) {
        viewModelScope.launch {
            removeConfigUseCase.execute(config)
        }
    }
}
