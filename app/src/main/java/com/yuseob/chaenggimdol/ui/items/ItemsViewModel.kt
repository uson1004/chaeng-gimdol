package com.yuseob.chaenggimdol.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItemsUiState(
    val items: List<UserItem> = emptyList(),
    val editorName: String = "",
    val errorMessage: String? = null,
    val retryAvailable: Boolean = false,
)

class ItemsViewModel(
    private val repository: ItemRepository,
) : ViewModel() {
    private val editor = MutableStateFlow("")
    private val error = MutableStateFlow<String?>(null)
    private val retryAvailable = MutableStateFlow(false)

    val state: StateFlow<ItemsUiState> = combine(
        repository.observeItems(),
        editor,
        error,
        retryAvailable,
    ) { items, name, message, canRetry ->
        ItemsUiState(
            items = items,
            editorName = name,
            errorMessage = message,
            retryAvailable = canRetry,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ItemsUiState(),
    )

    fun setEditorName(value: String) {
        editor.value = value
        error.value = null
        retryAvailable.value = false
    }

    fun saveEditor() {
        val name = editor.value.trim()
        if (name.isBlank()) {
            error.value = "물건 이름을 입력해 주세요."
            return
        }

        viewModelScope.launch {
            retryAvailable.value = false
            runCatching {
                repository.upsert(
                    UserItem(
                        name = name,
                        category = "other",
                    ),
                )
            }.onSuccess {
                editor.value = ""
            }.onFailure {
                error.value = "저장하지 못했어요. 다시 시도해 주세요."
                retryAvailable.value = true
            }
        }
    }

    fun retrySave() {
        if (retryAvailable.value) {
            saveEditor()
        }
    }

    fun toggleActive(item: UserItem) {
        viewModelScope.launch {
            repository.upsert(item.copy(active = !item.active))
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}
