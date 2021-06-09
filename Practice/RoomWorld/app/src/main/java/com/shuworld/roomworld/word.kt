package com.shuworld.roomworld

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class word(val word: String) {
    @Entity(tableName = "word_table")
    class Word(@PrimaryKey @ColumnInfo(name = "word") val word: String)
}