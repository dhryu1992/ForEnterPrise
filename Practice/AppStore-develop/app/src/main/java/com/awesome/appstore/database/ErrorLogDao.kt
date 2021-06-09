package com.awesome.appstore.database

import androidx.room.*
import rx.Completable

@Dao
interface ErrorLogDao {

    @Query("select * from ErrorLog")
    suspend fun getErrorLogAllList(): List<ErrorLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(errorLog: ErrorLog)

    @Delete
    suspend fun deleteErrorLog(errorLog: ErrorLog)

    @Query("delete from ErrorLog")
    suspend fun deleteAllErrorLog()
}
