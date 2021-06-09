package com.awesomebly.template.android.module

import android.content.Context
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
 * FileName : ApplicationModule
 * Date : 2021-05-03, 오전 10:13
 * History
seq   date          contents      programmer
01.   2021-05-03    init            차태준
02.
03.
 */

@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext appContext: Context): Context{
        return appContext
    }
}