package com.poly.hinote.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.poly.hinote.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes_table ORDER BY id ASC")
    fun readAllNotes(): Flow<List<Note>>

//    @Query("SELECT * FROM stocks_table WHERE isFavourite = 1 ORDER BY id ASC")
//    fun readFavouriteStocks(): LiveData<List<Stock>>

//    @Query("SELECT * FROM stocks_table WHERE ticker LIKE :searchQuery OR name LIKE :searchQuery")
//    fun searchRequest(searchQuery: String): LiveData<List<Stock>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteById(note: Note)

    @Query("DELETE FROM notes_table")
    suspend fun nukeTable()
}