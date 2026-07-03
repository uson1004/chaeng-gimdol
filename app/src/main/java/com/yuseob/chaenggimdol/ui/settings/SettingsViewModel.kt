package com.yuseob.chaenggimdol.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuseob.chaenggimdol.data.settings.AppPreferencesRepository
import com.yuseob.chaenggimdol.location.LocationSessionController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: AppPreferencesRepository,
    private val locationController: LocationSessionController,
    private val clearAllData: suspend () -> Unit,
) : ViewModel() {
    private val showDeleteConfirmation = MutableStateFlow(false)

    val state = combine(
        preferences.isLocationTrackingEnabled,
        showDeleteConfirmation,
    ) { trackingEnabled, showDelete ->
        SettingsUiState(
            locationTrackingEnabled = trackingEnabled,
            showDeleteConfirmation = showDelete,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    fun setLocationTrackingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (!enabled) {
                locationController.stop()
            }
            preferences.setLocationTrackingEnabled(enabled)
        }
    }

    fun requestDelete() {
        showDeleteConfirmation.value = true
    }

    fun dismissDelete() {
        showDeleteConfirmation.value = false
    }

    fun confirmDelete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            clearAllData()
            showDeleteConfirmation.value = false
            onDeleted()
        }
    }
}
