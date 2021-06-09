package com.awesomebly.template.android.module

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.awesomebly.template.android.database.TpDatabase
import com.awesomebly.template.android.util.config.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : UtilModule
 * Date : 2021-05-03, 오후 3:48
 * History
seq   date          contents      programmer
01.   2021-05-03                     차태준
02.
03.
 */
@InstallIn(SingletonComponent::class)
@Module
object UtilModule {
    @Provides
    @Singleton
    fun provideDatabase(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(Constants.Companion.Name.PREFERENCE_NAME, Activity.MODE_PRIVATE)
    }
}