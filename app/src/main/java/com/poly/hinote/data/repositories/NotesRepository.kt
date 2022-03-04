package com.poly.hinote.data.repositories

import com.poly.hinote.data.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>

    //val readAllData: LiveData<List<Note>> = appDatabase.notesDao().readAllData()
    //val favouriteStocksData: LiveData<List<Stock>> = stocksDao.readFavouriteStocks()

    //private val _searchResultList = MutableLiveData<List<Stock>>()
    //val searchResultList: LiveData<List<Stock>> = _searchResultList

    suspend fun addNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun nukeTable()

    /*suspend fun loadStocks(): Resource<List<Stock>> {
        val result = finnhubDataSource.loadStocks()
        if (result.status is Status.Success) {
            result.data!!.forEach {
                if (!containsSymbol(it.ticker)) {
                    addStock(it)
                }
            }
        }
        return result
    }

    fun getSearchResult(request: String): LiveData<List<Stock>> {
        return stocksDao.searchRequest("%$request%")
    }

    fun updateSearchResult(list: List<Stock>) {
        _searchResultList.value = list
    }



    private fun containsSymbol(symbol: String): Boolean {
        val data = readAllData.value ?: return false
        for (stock in data) {
            if (stock.ticker == symbol) {
                return true
            }
        }
        return false
    }*/

}