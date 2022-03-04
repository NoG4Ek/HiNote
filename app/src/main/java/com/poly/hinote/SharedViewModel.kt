package com.poly.hinote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poly.hinote.data.model.Note
import com.poly.hinote.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {
    val noteList = MutableLiveData<List<Note>>()
    private val storageNoteList = mutableListOf<Note>()
    private val applyingFilters = mutableListOf<String>()

    fun applyFilters(filters: List<String>) {
        if (filters.isEmpty()) {
            noteList.setValue(storageNoteList)
        } else {
            val filNoteList = mutableListOf<Note>()
            val nFilNoteList = noteList.value!! as MutableList<Note>

            if (applyingFilters.size > filters.size) {
                nFilNoteList.clearAndAddAll(storageNoteList)
            }

            for (note in nFilNoteList) {
                var tagEquals = 0
                for (tag in note.tags.split(" ")) {
                    for (filter in filters) {
                        if (tag == filter) {
                            tagEquals++
                        }
                    }
                }
                if (tagEquals == filters.size) {
                    filNoteList.add(note)
                }
            }
            noteList.setValue(filNoteList)
        }

        applyingFilters.clearAndAddAll(filters)
    }

    fun readAllNotes() {
        viewModelScope.launch {
            notesRepository.getAllNotes().collect { notes ->
                noteList.postValue(notes)
                storageNoteList.clearAndAddAll(notes)
            }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.addNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.deleteNote(note)
        }
    }

    fun nukeTable() {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.nukeTable()
        }
    }

    private fun <E> MutableCollection<E>.clearAndAddAll(replace: Collection<E>) {
        clear()
        addAll(replace)
    }
}