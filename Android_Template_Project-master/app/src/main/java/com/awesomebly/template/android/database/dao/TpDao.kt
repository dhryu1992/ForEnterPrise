package com.awesomebly.template.android.database.dao

import androidx.room.*
import com.awesomebly.template.android.database.entity.TpEntity

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : TpDao
 * Date : 2021-05-03, 오후 2:35
 * History
seq   date          contents      programmer
01.   2021-05-03                   차태준
02.
03.
 */

@Dao
interface TpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tp: TpEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tps: TpEntity)

    @Delete
    suspend fun delete(tp: TpEntity)

    @Query("SELECT * FROM TpEntity WHERE id = :id")
    suspend fun selectById(id: Long): TpEntity

    @Query("SELECT * FROM TpEntity WHERE name = :name")
    suspend fun selectListByName(name: String): List<TpEntity?>

    @Query("SELECT * FROM TpEntity ORDER BY id DESC")
    suspend fun selectAll(): List<TpEntity?>

    @Query("DELETE FROM TpEntity")
    suspend fun deleteAll()
}