package com.example.pertemuan_12new

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDAO {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAll (): Flow<List<Note>>


    @Insert
    suspend fun insert(note: Note)


    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}