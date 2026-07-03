package com.yuseob.chaenggimdol.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuseob.chaenggimdol.data.settings.AppPreferencesRepository
import com.yuseob.chaenggimdol.domain.item.SeedDefaultItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val selected: Set<String> = setOf(
        "휴대폰",
        "이어폰",
        "충전기",
    ),
    val saving: Boolean = false,
    val errorMessage: String? = null,
)

class OnboardingViewModel(
    private val seedDefaultItems: SeedDefaultItemsUseCase,
    private val preferences: AppPreferencesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state

    fun toggle(name: String) {
        _state.update { current ->
            current.copy(
                selected = if (name in current.selected) {
                    current.selected - name
                } else {
                    current.selected + name
                },
                errorMessage = null,
            )
        }
    }

    fun complete(onCompleted: () -> Unit) {
        val selected = state.value.selected
        if (selected.isEmpty()) {
            _state.update {
                it.copy(errorMessage = "한 개 이상 선택해 주세요.")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(saving = true, errorMessage = null)
            }
            runCatching {
                seedDefaultItems(selected)
                preferences.setOnboardingComplete(true)
                preferences.setLocationTrackingEnabled(true)
            }.onSuccess {
                onCompleted()
            }.onFailure {
                _state.update { current ->
                    current.copy(
                        saving = false,
                        errorMessage = "저장하지 못했어요. 다시 시도해 주세요.",
                    )
                }
            }
        }
    }
}
