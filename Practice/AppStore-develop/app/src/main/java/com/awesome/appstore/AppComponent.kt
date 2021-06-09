package com.awesome.appstore

import com.awesome.appstore.module.*
import com.awesome.appstore.module.viewmodels.ViewModelsModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class,
    ActivityBuilder::class,
    AppModule::class,
    StoreModule::class,
    NetworkModule::class,
    ViewModelFactoryModule::class,
    ViewModelsModule::class,
    SecurityModule::class
    ])

interface AppComponent : AndroidInjector<AppStoreApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<AppStoreApplication?>() {
        abstract fun addAppModule(appModule: AppModule?): Builder?
        abstract fun addStoreModule(storeModule: StoreModule?): Builder?
        abstract fun addNetworkModule(networkModule: NetworkModule?): Builder?
        abstract fun addSecurityModule(securityModule: SecurityModule?): Builder?
        override fun seedInstance(instance: AppStoreApplication?) {
            addAppModule(AppModule(instance))
            addNetworkModule(NetworkModule(instance))
            addStoreModule(StoreModule(instance))
            addSecurityModule((SecurityModule()))
        }
    }
}