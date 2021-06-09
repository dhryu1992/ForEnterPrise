package com.awesomebly.template.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.awesomebly.template.android.database.dao.TpDao
import com.awesomebly.template.android.database.entity.TpEntity

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : TpDatabase
 * Date : 2021-05-03, 오후 2:06
 * History
seq   date          contents      programmer
01.   2021-05-03    init            차태준
02.
03.
 */
@Database(entities = [TpEntity::class], version = 1, exportSchema = false)
abstract class TpDatabase : RoomDatabase(){
    abstract fun tpDao(): TpDao
}
