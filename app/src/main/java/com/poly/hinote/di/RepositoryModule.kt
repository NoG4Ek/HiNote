package com.poly.hinote.di

import com.poly.hinote.data.repositories.NotesRepository
import com.poly.hinote.data.repositories.NotesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Repositories will live same as the activity that requires them
 * Bind Interface to Impl
 */
@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun providesNotesRepository(impl: NotesRepositoryImpl): NotesRepository

}