package com.poly.hinote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Main business class
 * Parameter description: TODO()
 */

@Entity(tableName = "notes_table")
data class Note (
    val shareData: String,
    val header: String,
    val tags: String,
    val complexText: String,
    val link: String,
    val time: String //https://medium.com/androiddevelopers/room-time-2b4cf9672b98
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}