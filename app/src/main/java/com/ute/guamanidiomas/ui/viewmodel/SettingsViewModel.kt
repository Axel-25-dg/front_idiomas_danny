package com.ute.guamanidiomas.ui.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
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

    val appLanguage: StateFlow<String> = tokenDataStore.appLanguage
        .stateIn(viewModelScope, SharingStarted.Eagerly, "es")

    val isSoundEnabled: StateFlow<Boolean> = tokenDataStore.isSoundEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isNotificationsEnabled: StateFlow<Boolean> = tokenDataStore.isNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

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

    fun setAppLanguage(lang: String) {
        viewModelScope.launch {
            tokenDataStore.setAppLanguage(lang)
            val localeList = LocaleListCompat.forLanguageTags(lang)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            tokenDataStore.setSoundEnabled(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            tokenDataStore.setNotificationsEnabled(enabled)
        }
    }
}