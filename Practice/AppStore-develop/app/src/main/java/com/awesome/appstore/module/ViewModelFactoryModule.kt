package com.awesome.appstore.module

import androidx.lifecycle.ViewModelProvider
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory


}