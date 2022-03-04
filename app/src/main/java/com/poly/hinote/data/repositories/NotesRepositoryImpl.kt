package com.poly.hinote.data.repositories

import com.poly.hinote.data.database.NotesDao
import com.poly.hinote.data.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(private val notesDao : NotesDao) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.readAllNotes()
    }

   override suspend fun addNote(note: Note) {
        notesDao.addNote(note)
    }

    override suspend fun updateNote(note: Note) {
        notesDao.updateNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        notesDao.deleteNote(note)
    }

    //Temporary solution
    override suspend fun nukeTable() {
        notesDao.nukeTable()
    }
}