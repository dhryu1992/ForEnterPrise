package com.awesome.appstore.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PushCountDao {
    @Query("SELECT * FROM PushCount")
    suspend fun getAllPushCount(): List<PushCount?>

    @Query("SELECT * FROM PushCount WHERE packageName = :pkgName")
    suspend fun getPushCount(pkgName: String): PushCount

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(PushCount: PushCount?): Long
}
