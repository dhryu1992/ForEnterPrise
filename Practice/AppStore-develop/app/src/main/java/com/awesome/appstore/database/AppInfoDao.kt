package com.awesome.appstore.database

import androidx.room.*

@Dao
interface AppInfoDao {

    @Query("SELECT * FROM AppInfo order by installDate")
    suspend fun allAppInfo(): List<AppInfo?>

    @Query("SELECT * FROM AppInfo WHERE installStatus = 'Y'")
    suspend fun installedAppInfo(): List<AppInfo?>

    @Query("SELECT * FROM AppInfo WHERE updateStatus = 'Y'")
    suspend fun updateAppInfo(): List<AppInfo?>

    @Query("SELECT * FROM AppInfo WHERE releaseType = 'M' order by installDate")
    suspend fun essentialAppInfo(): List<AppInfo?>

    @Query("SELECT * FROM AppInfo WHERE packageName = :pkgName")
    suspend fun selectAppInfoByPackageName(pkgName : String): AppInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppInfo(appInfo: AppInfo?):Long

    @Delete
    suspend fun deleteAppInfo(appInfo: AppInfo?)

    @Query("delete from AppInfo")
    suspend fun deleteAllAppInfo()
}
