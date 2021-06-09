package com.awesomebly.template.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : TpEntity
 * Date : 2021-05-03, 오후 2:11
 * History
seq   date          contents      programmer
01.   2021-05-03                    차태준
02.
03.
 */
@Entity
data class TpEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val date: String
)