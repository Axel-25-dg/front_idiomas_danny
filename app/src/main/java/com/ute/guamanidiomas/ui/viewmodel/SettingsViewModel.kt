package com.ute.guamanidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.guamanidiomas.data.local.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = tokenDataStore.isDarkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleDarkMode() {
        viewModelScope.launch {
            tokenDataStore.setDarkMode(!isDarkMode.value)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            tokenDataStore.setDarkMode(enabled)
        }
    }
}
