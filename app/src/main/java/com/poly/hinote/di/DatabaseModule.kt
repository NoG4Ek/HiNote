package com.poly.hinote.di

import android.content.Context
import androidx.room.Room
import com.poly.hinote.data.database.AppDatabase
import com.poly.hinote.data.database.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Rules of providing DAO and App Database instances
 */

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideNotesDao(appDatabase: AppDatabase): NotesDao {
        return appDatabase.notesDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "notesDB"
        ).build()
    }
}