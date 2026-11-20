package com.example.pertemuan_12new

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.pertemuan_12new.ui.theme.Pertemuan_12NewTheme
import org.junit.Rule
import org.junit.Test

class NotesScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun createEditDeleteAndThemeToggleWork() {
        var notes by mutableStateOf(emptyList<Note>())
        var darkTheme by mutableStateOf(false)
        var nextId = 1L

        composeRule.setContent {
            Pertemuan_12NewTheme(darkTheme = darkTheme) {
                NotesScreenContent(
                    uiState = NotesUiState.Success(notes),
                    actionError = null,
                    isDarkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme },
                    onAddNote = { title, body ->
                        notes = listOf(Note(id = nextId++, title = title.trim(), note = body.trim())) + notes
                    },
                    onUpdateNote = { existing, newTitle, newBody ->
                        notes = notes.map {
                            if (it.id == existing.id) it.copy(title = newTitle.trim(), note = newBody.trim()) else it
                        }
                    },
                    onDeleteNote = { target ->
                        notes = notes.filterNot { it.id == target.id }
                    },
                    onRetryLoad = {},
                    onDismissActionError = {}
                )
            }
        }

        // Empty state and validation on empty submit
        composeRule.onNodeWithText("Belum ada catatan tersimpan").assertIsDisplayed()
        composeRule.onNodeWithText("Simpan Catatan").performClick()
        composeRule.onNodeWithText("Judul wajib diisi").assertIsDisplayed()
        composeRule.onNodeWithText("Isi catatan wajib diisi").assertIsDisplayed()

        // Create note with title and note
        composeRule.onNodeWithText("Judul").performTextInput("Tugas PPPBD")
        composeRule.onNodeWithText("Catatan").performTextInput("Tambahkan field title dan note")
        composeRule.onNodeWithText("Simpan Catatan").performClick()

        composeRule.onNodeWithText("Tugas PPPBD").assertIsDisplayed()
        composeRule.onNodeWithText("Tambahkan field title dan note").assertIsDisplayed()
        composeRule.onNodeWithText("ID 1").assertIsDisplayed()

        // Edit title and note
        composeRule.onNodeWithText("Edit").performClick()
        composeRule.onNodeWithText("Edit Catatan (ID 1)").assertIsDisplayed()
        composeRule.onNodeWithText("Tugas PPPBD").performTextClearance()
        composeRule.onNodeWithText("Tambahkan field title dan note").performTextClearance()
        composeRule.onNodeWithText("Simpan").performClick()
        composeRule.onNodeWithText("Judul wajib diisi").assertIsDisplayed()

        // Cancel edit and verify original remains
        composeRule.onNodeWithText("Batal").performClick()
        composeRule.onNodeWithText("Tugas PPPBD").assertIsDisplayed()

        // Theme toggle
        composeRule.onNodeWithText("Mode Gelap").performClick()
        composeRule.onNodeWithText("Mode Terang").assertIsDisplayed()

        // Delete note
        composeRule.onNodeWithText("Hapus").performClick()
        composeRule.onNodeWithText("Belum ada catatan tersimpan").assertIsDisplayed()
    }
}
