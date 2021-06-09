package com.awesome.appstore.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoticeDao {
    @Query("SELECT * FROM Notice")
    suspend fun getNoticeAllList():List<Notice?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notice: Notice?): Long

    @Query("delete from Notice")
    suspend fun deleteAllNotice()
}
