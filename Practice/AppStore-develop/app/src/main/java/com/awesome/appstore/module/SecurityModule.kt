package com.awesome.appstore.module

import com.awesome.appstore.AppStoreApplication
import com.awesome.appstore.security.keyboard.ExafeKeySecE2EManager
import com.awesome.appstore.security.mdm.MDMHelper
import com.awesome.appstore.security.sslvpn.SecuwaySSLHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SecurityModule() {

    @Provides
    @Singleton
    fun provideMdmHelper(): MDMHelper {
        return MDMHelper()
    }

    @Provides
    @Singleton
    fun provideSecuwaySSLHelper(): SecuwaySSLHelper{
        return  SecuwaySSLHelper()
    }

//    @Provides
//    @Singleton
//    fun provideExafeKeySecE2EManager(): ExafeKeySecE2EManager{
//        return ExafeKeySecE2EManager()
//    }
}