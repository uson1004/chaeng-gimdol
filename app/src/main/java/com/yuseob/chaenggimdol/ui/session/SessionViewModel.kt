package com.yuseob.chaenggimdol.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuseob.chaenggimdol.domain.session.CheckSessionItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.domain.session.CompleteCheckSessionUseCase
import com.yuseob.chaenggimdol.domain.session.SessionRepository
import com.yuseob.chaenggimdol.location.LocationSessionController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionUiState(
    val sessionId: Long = 0,
    val items: List<CheckSessionItem> = emptyList(),
    val confirmIncompleteCount: Int? = null,
    val completed: Boolean = false,
) {
    val uncheckedCount: Int
        get() = items.count { it.status == CheckStatus.Unchecked }

    val checkedCount: Int
        get() = items.size - uncheckedCount

    val uncheckedImportantCount: Int
        get() = items.count { it.important && it.status == CheckStatus.Unchecked }

    val uncheckedOptionalCount: Int
        get() = items.count { !it.important && it.status == CheckStatus.Unchecked }
}

class SessionViewModel(
    private val sessionId: Long,
    private val repository: SessionRepository,
    private val completeSession: CompleteCheckSessionUseCase,
    private val locationController: LocationSessionController,
) : ViewModel() {
    private val _state = MutableStateFlow(
        SessionUiState(sessionId = sessionId),
    )
    val state: StateFlow<SessionUiState> = _state

    init {
        viewModelScope.launch {
            repository.get(sessionId)?.let { session ->
                _state.update {
                    it.copy(items = session.items)
                }
            }
        }
    }

    fun togglePacked(itemId: Long) {
        val item = state.value.items.first {
            it.itemId == itemId
        }
        val next = if (item.status == CheckStatus.Packed) {
            CheckStatus.Unchecked
        } else {
            CheckStatus.Packed
        }
        updateStatus(itemId, next)
    }

    fun markNotApplicable(itemId: Long) {
        updateStatus(itemId, CheckStatus.NotApplicable)
    }

    fun markAllPacked() {
        viewModelScope.launch {
            state.value.items
                .filter { it.status == CheckStatus.Unchecked }
                .forEach { item ->
                    repository.setItemStatus(sessionId, item.itemId, CheckStatus.Packed)
                }
            _state.update { current ->
                current.copy(
                    items = current.items.map { item ->
                        if (item.status == CheckStatus.Unchecked) {
                            item.copy(status = CheckStatus.Packed)
                        } else {
                            item
                        }
                    },
                )
            }
        }
    }

    private fun updateStatus(
        itemId: Long,
        status: CheckStatus,
    ) {
        viewModelScope.launch {
            repository.setItemStatus(sessionId, itemId, status)
            _state.update { current ->
                current.copy(
                    items = current.items.map { item ->
                        if (item.itemId == itemId) {
                            item.copy(status = status)
                        } else {
                            item
                        }
                    },
                )
            }
        }
    }

    fun requestComplete() {
        val unchecked = state.value.items.count {
            it.status == CheckStatus.Unchecked
        }
        if (unchecked > 0) {
            _state.update {
                it.copy(confirmIncompleteCount = unchecked)
            }
        } else {
            completeConfirmed()
        }
    }

    fun dismissIncomplete() {
        _state.update {
            it.copy(confirmIncompleteCount = null)
        }
    }

    fun completeConfirmed() {
        viewModelScope.launch {
            completeSession(sessionId)
            locationController.stop()
            _state.update {
                it.copy(
                    confirmIncompleteCount = null,
                    completed = true,
                )
            }
        }
    }
}
