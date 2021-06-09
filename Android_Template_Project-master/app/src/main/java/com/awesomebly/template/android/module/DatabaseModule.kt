package com.awesomebly.template.android.module

import android.content.Context
import androidx.room.Room
import com.awesomebly.template.android.database.TpDatabase
import com.awesomebly.template.android.database.dao.TpDao
import com.awesomebly.template.android.database.handler.DatabaseHandler
import com.awesomebly.template.android.util.config.Constants.Companion.Name.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : DatabaseModule
 * Date : 2021-05-03, 오후 2:16
 * History
seq   date          contents      programmer
01.   2021-05-03                    차태준
02.
03.
 */

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(appContext: Context): TpDatabase {
        return Room.databaseBuilder(appContext, TpDatabase::class.java, DATABASE_NAME)
            .build()
    }

    @Provides
    fun provideTpDao(database: TpDatabase): TpDao {
        return database.tpDao()
    }

    @Provides
    fun provideDatabaseHandler(): DatabaseHandler {
        return DatabaseHandler()
    }
}