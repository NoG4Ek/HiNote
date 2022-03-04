package com.poly.hinote.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.poly.hinote.data.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao
}