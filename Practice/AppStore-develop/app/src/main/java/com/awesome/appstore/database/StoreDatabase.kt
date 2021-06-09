package com.awesome.appstore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.awesome.appstore.config.StoreConst

@Database(
    entities = [AppInfo::class, ErrorLog::class, Notice::class, LauncherIndex::class, PushCount::class],
    version = 19,
    exportSchema = false
)
abstract class StoreDatabase : RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao
    abstract fun errorLogDao(): ErrorLogDao
    abstract fun noticeDao(): NoticeDao
    abstract fun launcherIndexDao(): LauncherIndexDao
    abstract fun pushCountDao(): PushCountDao

    companion object {
        @Volatile
        private var instance: StoreDatabase? = null

        private val LOCK = Any()

        //        @Synchronized
        fun getInstance(context: Context): StoreDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                StoreDatabase::class.java, StoreConst.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: getInstance(context).also { instance = it }
        }
    }
}