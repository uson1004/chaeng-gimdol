package com.yuseob.chaenggimdol.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_preferences")

class AppPreferencesRepository(
    private val context: Context,
) {
    private val onboardingComplete = booleanPreferencesKey("onboarding_complete")
    private val locationTrackingEnabled = booleanPreferencesKey("location_tracking_enabled")

    val isOnboardingComplete: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[onboardingComplete] ?: false
        }

    val isLocationTrackingEnabled: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[locationTrackingEnabled] ?: false
        }

    suspend fun setOnboardingComplete(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[onboardingComplete] = value
        }
    }

    suspend fun setLocationTrackingEnabled(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[locationTrackingEnabled] = value
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
