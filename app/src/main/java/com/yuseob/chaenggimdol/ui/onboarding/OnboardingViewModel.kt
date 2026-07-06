package com.yuseob.chaenggimdol.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuseob.chaenggimdol.data.settings.AppPreferencesRepository
import com.yuseob.chaenggimdol.domain.item.SeedDefaultItemsUseCase
import com.yuseob.chaenggimdol.domain.item.SeedItem
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
    val optional: Set<String> = emptySet(),
    val hints: Map<String, String> = emptyMap(),
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
            val selected = if (name in current.selected) {
                current.selected - name
            } else {
                current.selected + name
            }
            current.copy(
                selected = selected,
                optional = current.optional.intersect(selected),
                hints = current.hints.filterKeys { it in selected },
                errorMessage = null,
            )
        }
    }

    fun toggleImportant(name: String) {
        _state.update { current ->
            current.copy(
                optional = if (name in current.optional) {
                    current.optional - name
                } else {
                    current.optional + name
                },
                errorMessage = null,
            )
        }
    }

    fun setHint(name: String, hint: String) {
        _state.update { current ->
            current.copy(
                hints = current.hints + (name to hint),
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
                seedDefaultItems(
                    selected.map { name ->
                        SeedItem(
                            name = name,
                            important = name !in state.value.optional,
                            checkHint = state.value.hints[name],
                        )
                    },
                )
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
