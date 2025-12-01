package com.aki.app.viewmodel

import androidx.lifecycle.viewModelScope
import com.aki.app.base.BaseViewModel
import com.aki.core.domain.usecase.GetGreetingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the [com.aki.akiproxy.app.view.MainActivity].
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getGreetingUseCase: GetGreetingUseCase
) : BaseViewModel() {

    private val _greeting = MutableStateFlow("Loading...")
    val greeting: StateFlow<String> = _greeting

    init {
        loadGreeting()
    }

    private fun loadGreeting() {
        viewModelScope.launch {
            _greeting.value = getGreetingUseCase.execute(Unit)
        }
    }
}
