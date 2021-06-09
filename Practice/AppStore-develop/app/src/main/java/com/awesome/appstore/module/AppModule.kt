package com.awesome.appstore.module

import android.app.Application
import com.awesome.appstore.AppStoreApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: AppStoreApplication?) {

    @Provides
    @Singleton
    fun provideApplication() : AppStoreApplication? = application
}