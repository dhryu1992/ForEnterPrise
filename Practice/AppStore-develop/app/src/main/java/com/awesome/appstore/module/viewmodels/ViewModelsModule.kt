package com.awesome.appstore.module.viewmodels

import androidx.lifecycle.ViewModel
import com.awesome.appstore.activity.viewmodel.*
import com.awesome.push.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindsMainViewModel(mainActivityViewModel: MainActivityViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(LoginActivityViewModel::class)
    abstract fun bindsLoginActivityViewModel(loginActivityViewModel: LoginActivityViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(TabActivityViewModel::class)
    abstract fun bindsTabActivityViewModel(tabActivityViewModel: TabActivityViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(TabExecuteFragmentViewModel::class)
    abstract fun bindsTabExecuteFragmentViewModel(tabExecuteFragmentViewModel: TabExecuteFragmentViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(TabAllFragmentViewModel::class)
    abstract fun bindsTabAllFragmentViewModel(tabAllFragmentViewModel: TabAllFragmentViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(TabEssentialFragmentViewModel::class)
    abstract fun bindsTabEssentialFragmentViewModel(tabEssentialFragmentViewModel: TabEssentialFragmentViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(TabUpdateFragmentViewModel::class)
    abstract fun bindsTabUpdateFragmentViewModel(tabUpdateFragmentViewModel: TabUpdateFragmentViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(DetailActivityViewModel::class)
    abstract fun bindsDetailActivityViewModel(detailActivityViewModel: DetailActivityViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(NoticeActivityViewModel::class)
    abstract fun bindsNoticeActivityViewModel(noticeActivityViewModel: NoticeActivityViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(ErrorLogActivityViewModel::class)
    abstract fun bindsErrorLogActivityViewModel(errorLogActivityViewModel: ErrorLogActivityViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(SettingActivityViewModel::class)
    abstract fun bindsSettingActivityViewModel(settingActivityViewModel: SettingActivityViewModel?): ViewModel?
}