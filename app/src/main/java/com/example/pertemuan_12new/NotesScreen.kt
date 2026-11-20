package com.example.pertemuan_12new

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pertemuan_12new.ui.theme.Pertemuan_12NewTheme

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    vm: NotesViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val actionError by vm.actionError.collectAsState()

    NotesScreenContent(
        modifier = modifier,
        uiState = uiState,
        actionError = actionError,
        isDarkTheme = isDarkTheme,
        onToggleTheme = onToggleTheme,
        onAddNote = { title, note -> vm.addNote(title, note) },
        onUpdateNote = { existing, newTitle, newNote -> vm.updateNote(existing, newTitle, newNote) },
        onDeleteNote = { note -> vm.deleteNote(note) },
        onRetryLoad = { vm.retryLoad() },
        onDismissActionError = { vm.clearActionError() }
    )
}

@Composable
fun NotesScreenContent(
    uiState: NotesUiState,
    actionError: String?,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onAddNote: (title: String, note: String) -> Unit,
    onUpdateNote: (existing: Note, newTitle: String, newNote: String) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onRetryLoad: () -> Unit,
    onDismissActionError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var titleInput by rememberSaveable { mutableStateOf("") }
    var noteInput by rememberSaveable { mutableStateOf("") }
    var createTitleError by rememberSaveable { mutableStateOf<String?>(null) }
    var createNoteError by rememberSaveable { mutableStateOf<String?>(null) }

    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingTitle by rememberSaveable { mutableStateOf("") }
    var editingNote by rememberSaveable { mutableStateOf("") }
    var editTitleError by rememberSaveable { mutableStateOf<String?>(null) }
    var editNoteError by rememberSaveable { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Catatan Saya",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.semantics { heading() }
                        )
                        if (uiState is NotesUiState.Success) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Jumlah catatan: ${uiState.notes.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = onToggleTheme,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text(
                            text = if (isDarkTheme) "Mode Terang" else "Mode Gelap",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (actionError != null) {
                item(key = "action_error") {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = actionError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            TextButton(
                                onClick = onDismissActionError,
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Text(
                                    text = "Tutup",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            item(key = "create_form") {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Tambah Catatan",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = {
                                titleInput = it
                                if (createTitleError != null && it.isNotBlank()) {
                                    createTitleError = null
                                }
                            },
                            label = { Text("Judul") },
                            placeholder = { Text("Tulis judul catatan") },
                            singleLine = true,
                            isError = createTitleError != null,
                            supportingText = createTitleError?.let { msg ->
                                { Text(msg) }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = noteInput,
                            onValueChange = {
                                noteInput = it
                                if (createNoteError != null && it.isNotBlank()) {
                                    createNoteError = null
                                }
                            },
                            label = { Text("Catatan") },
                            placeholder = { Text("Tulis isi catatan di sini") },
                            minLines = 2,
                            maxLines = 4,
                            isError = createNoteError != null,
                            supportingText = createNoteError?.let { msg ->
                                { Text(msg) }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    val titleBlank = titleInput.isBlank()
                                    val noteBlank = noteInput.isBlank()
                                    createTitleError = if (titleBlank) "Judul wajib diisi" else null
                                    createNoteError = if (noteBlank) "Isi catatan wajib diisi" else null
                                    if (!titleBlank && !noteBlank) {
                                        onAddNote(titleInput, noteInput)
                                        titleInput = ""
                                        noteInput = ""
                                        focusManager.clearFocus()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add),
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                Text("Simpan Catatan")
                            }
                        }
                    }
                }
            }

            when (uiState) {
                is NotesUiState.Loading -> {
                    item(key = "loading_state") {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = "Memuat daftar catatan...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                is NotesUiState.Error -> {
                    item(key = "error_state") {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Daftar catatan gagal dimuat",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = uiState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                OutlinedButton(
                                    onClick = onRetryLoad,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text(
                                        text = "Coba Lagi",
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                is NotesUiState.Success -> {
                    if (uiState.notes.isEmpty()) {
                        item(key = "empty_state") {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Belum ada catatan tersimpan",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Isi judul dan catatan pada formulir di atas, lalu tekan Simpan Catatan untuk mulai menyimpan.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        item(key = "list_heading") {
                            Text(
                                text = "Daftar Catatan",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .semantics { heading() }
                            )
                        }
                        items(uiState.notes, key = { it.id }) { note ->
                            if (editingId == note.id) {
                                NoteItemEditing(
                                    noteId = note.id,
                                    title = editingTitle,
                                    noteText = editingNote,
                                    titleError = editTitleError,
                                    noteError = editNoteError,
                                    onTitleChange = {
                                        editingTitle = it
                                        if (editTitleError != null && it.isNotBlank()) {
                                            editTitleError = null
                                        }
                                    },
                                    onNoteChange = {
                                        editingNote = it
                                        if (editNoteError != null && it.isNotBlank()) {
                                            editNoteError = null
                                        }
                                    },
                                    onSave = {
                                        val titleBlank = editingTitle.isBlank()
                                        val noteBlank = editingNote.isBlank()
                                        editTitleError =
                                            if (titleBlank) "Judul wajib diisi" else null
                                        editNoteError =
                                            if (noteBlank) "Isi catatan wajib diisi" else null
                                        if (!titleBlank && !noteBlank) {
                                            onUpdateNote(note, editingTitle, editingNote)
                                            editingId = null
                                            editTitleError = null
                                            editNoteError = null
                                            focusManager.clearFocus()
                                        }
                                    },
                                    onCancel = {
                                        editingId = null
                                        editTitleError = null
                                        editNoteError = null
                                        focusManager.clearFocus()
                                    }
                                )
                            } else {
                                NoteItem(
                                    note = note,
                                    onEdit = {
                                        editingId = note.id
                                        editingTitle = note.title
                                        editingNote = note.note
                                        editTitleError = null
                                        editNoteError = null
                                    },
                                    onDelete = {
                                        if (editingId == note.id) {
                                            editingId = null
                                        }
                                        onDeleteNote(note)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ID ${note.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = note.note,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onEdit,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Edit",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItemEditing(
    noteId: Long,
    title: String,
    noteText: String,
    titleError: String?,
    noteError: String?,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Escape) {
                    onCancel()
                    true
                } else {
                    false
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Edit Catatan (ID $noteId)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Judul") },
                singleLine = true,
                isError = titleError != null,
                supportingText = titleError?.let { msg -> { Text(msg) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteChange,
                label = { Text("Catatan") },
                minLines = 2,
                maxLines = 5,
                isError = noteError != null,
                supportingText = noteError?.let { msg -> { Text(msg) } },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Batal",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Button(
                    onClick = onSave,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Simpan")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotesScreenLightPreview() {
    Pertemuan_12NewTheme(darkTheme = false) {
        NotesScreenContent(
            uiState = NotesUiState.Success(
                listOf(
                    Note(
                        id = 1,
                        title = "Belajar Room Database",
                        note = "Tambahkan kolom note pada entity dan buat migrasi versi 1 ke 2."
                    )
                )
            ),
            actionError = null,
            isDarkTheme = false,
            onToggleTheme = {},
            onAddNote = { _, _ -> },
            onUpdateNote = { _, _, _ -> },
            onDeleteNote = {},
            onRetryLoad = {},
            onDismissActionError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotesScreenDarkPreview() {
    Pertemuan_12NewTheme(darkTheme = true) {
        NotesScreenContent(
            uiState = NotesUiState.Success(
                listOf(
                    Note(
                        id = 2,
                        title = "Catatan Praktikum",
                        note = "Pastikan form tambah dan edit menampilkan field judul serta isi catatan."
                    )
                )
            ),
            actionError = null,
            isDarkTheme = true,
            onToggleTheme = {},
            onAddNote = { _, _ -> },
            onUpdateNote = { _, _, _ -> },
            onDeleteNote = {},
            onRetryLoad = {},
            onDismissActionError = {}
        )
    }
}