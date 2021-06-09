package com.awesome.appstore.database

import androidx.room.*

@Dao
interface LauncherIndexDao {
    @Query("SELECT * FROM LauncherIndex")
    suspend fun getIndexAll(): List<LauncherIndex?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(index: LauncherIndex?): Long?

    @Query("DELETE FROM LauncherIndex WHERE packageName = :pkgName")
    suspend fun deleteByPkgName(pkgName: String)
}
