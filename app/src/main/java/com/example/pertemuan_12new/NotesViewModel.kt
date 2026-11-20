package com.example.pertemuan_12new

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data class Success(val notes: List<Note>) : NotesUiState
    data class Error(val message: String) : NotesUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).noteDAO()
    private val reloadTrigger = MutableStateFlow(0)

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    // READ: Flow dari Room diubah menjadi StateFlow berstatus Loading/Success/Error
    val uiState: StateFlow<NotesUiState> = reloadTrigger
        .flatMapLatest {
            dao.getAll()
                .map<List<Note>, NotesUiState> { list -> NotesUiState.Success(list) }
                .onStart { emit(NotesUiState.Loading) }
                .catch { e ->
                    emit(
                        NotesUiState.Error(
                            e.localizedMessage ?: "Gagal memuat daftar catatan dari database."
                        )
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesUiState.Loading
        )

    fun retryLoad() {
        reloadTrigger.value += 1
    }

    fun clearActionError() {
        _actionError.value = null
    }

    // CREATE
    fun addNote(title: String, note: String) {
        val cleanTitle = title.trim()
        val cleanNote = note.trim()
        if (cleanTitle.isEmpty() || cleanNote.isEmpty()) return
        viewModelScope.launch {
            try {
                _actionError.value = null
                dao.insert(Note(title = cleanTitle, note = cleanNote))
            } catch (e: Exception) {
                _actionError.value =
                    e.localizedMessage ?: "Gagal menyimpan catatan baru."
            }
        }
    }

    // UPDATE
    fun updateNote(existingNote: Note, newTitle: String, newNote: String) {
        val cleanTitle = newTitle.trim()
        val cleanNote = newNote.trim()
        if (cleanTitle.isEmpty() || cleanNote.isEmpty()) return
        viewModelScope.launch {
            try {
                _actionError.value = null
                dao.update(existingNote.copy(title = cleanTitle, note = cleanNote))
            } catch (e: Exception) {
                _actionError.value =
                    e.localizedMessage ?: "Gagal memperbarui catatan."
            }
        }
    }

    // DELETE
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                _actionError.value = null
                dao.delete(note)
            } catch (e: Exception) {
                _actionError.value =
                    e.localizedMessage ?: "Gagal menghapus catatan."
            }
        }
    }
}