package org.emmadice.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.emmadice.app.data.db.CardEntity

class CardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CardRepository(application)

    val cards: StateFlow<List<CardEntity>> = repository.cards.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val messages: SharedFlow<String> = _messages

    init {
        viewModelScope.launch {
            runCatching { repository.initialize() }
                .onFailure { _messages.emit(messageFor(it)) }
        }
    }

    fun create(title: String, onResult: (CardEntity?) -> Unit = {}) = viewModelScope.launch {
        runCatching { repository.create(title) }
            .onSuccess(onResult)
            .onFailure { _messages.emit(messageFor(it)); onResult(null) }
    }

    fun update(
        card: CardEntity,
        onError: (Throwable) -> Unit = {},
        onSuccess: () -> Unit = {},
        reportMessage: Boolean = true
    ) = viewModelScope.launch {
        runCatching { repository.update(card) }
            .onSuccess { onSuccess() }
            .onFailure {
                if (reportMessage) _messages.emit(messageFor(it))
                onError(it)
            }
    }

    fun setImagePath(card: CardEntity, path: String?, onError: (Throwable) -> Unit = {}) =
        viewModelScope.launch {
            runCatching { repository.setImagePath(card, path) }.onFailure { _messages.emit(messageFor(it)); onError(it) }
        }

    fun setAudioPath(card: CardEntity, path: String?, onError: (Throwable) -> Unit = {}) =
        viewModelScope.launch {
            runCatching { repository.setAudioPath(card, path) }.onFailure { _messages.emit(messageFor(it)); onError(it) }
        }

    fun delete(card: CardEntity, onError: (Throwable) -> Unit = {}) = viewModelScope.launch {
        runCatching { repository.delete(card) }.onFailure { _messages.emit(messageFor(it)); onError(it) }
    }

    fun resources(): CardResources = repository.resources()

    private fun messageFor(error: Throwable): String =
        error.message?.takeIf { it.isNotBlank() } ?: "No se pudo guardar la tarjeta"
}
