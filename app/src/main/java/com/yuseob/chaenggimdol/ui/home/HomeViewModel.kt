package com.yuseob.chaenggimdol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.domain.session.SessionRepository
import com.yuseob.chaenggimdol.domain.session.StartCheckSessionUseCase
import com.yuseob.chaenggimdol.location.LocationSessionController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val activeItems: List<UserItem> = emptyList(),
    val activeSessionId: Long? = null,
    val message: String? = null,
)

sealed interface HomeEffect {
    data class NavigateToSession(
        val sessionId: Long,
    ) : HomeEffect
}

class HomeViewModel(
    itemRepository: ItemRepository,
    sessionRepository: SessionRepository,
    private val startCheckSession: StartCheckSessionUseCase,
    private val locationController: LocationSessionController,
    private val canStartTracking: () -> Boolean,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    private val effectChannel = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init {
        combine(
            itemRepository.observeItems(),
            sessionRepository.observeActive(),
        ) { items, session ->
            items.filter { it.active } to session?.id
        }.onEach { (items, sessionId) ->
            _state.update {
                it.copy(
                    activeItems = items,
                    activeSessionId = sessionId,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun startSession() {
        val items = state.value.activeItems
        if (items.isEmpty()) {
            _state.update {
                it.copy(
                    message = "먼저 챙길 물건을 등록해 주세요.",
                )
            }
            return
        }

        viewModelScope.launch {
            val id = startCheckSession(items)
            if (canStartTracking()) {
                locationController.start(id)
            }
            effectChannel.send(HomeEffect.NavigateToSession(id))
        }
    }

    fun resumeSession() {
        state.value.activeSessionId?.let { id ->
            effectChannel.trySend(HomeEffect.NavigateToSession(id))
        }
    }

    fun showMessage(message: String) {
        _state.update { it.copy(message = message) }
    }
}
