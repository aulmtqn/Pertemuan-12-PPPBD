package com.example.pertemuan_12new

import org.junit.Assert.assertEquals
import org.junit.Test

class NoteTest {
    @Test
    fun noteStoresIdTitleAndNote() {
        val item = Note(title = "Belanja", note = "Beras dan telur")
        assertEquals(0L, item.id)
        assertEquals("Belanja", item.title)
        assertEquals("Beras dan telur", item.note)

        val updated = item.copy(id = 5L, title = "Belanja Mingguan", note = "Beras, telur, susu")
        assertEquals(5L, updated.id)
        assertEquals("Belanja Mingguan", updated.title)
        assertEquals("Beras, telur, susu", updated.note)
    }
}
