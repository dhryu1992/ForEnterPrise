package com.awesome.appstore.module

import com.awesome.appstore.activity.*
import com.awesome.appstore.activity.fragment.TabAllFragment
import com.awesome.appstore.activity.fragment.TabEssentialFragment
import com.awesome.appstore.activity.fragment.TabExecuteFragment
import com.awesome.appstore.activity.fragment.TabUpdateFragment
import com.awesome.appstore.fcm.PushReceiver
import com.awesome.appstore.util.errlog.ErrorLogReporter
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindErrorLogReporter(): ErrorLogReporter

    @ContributesAndroidInjector
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    abstract fun bindTabActivity(): TabActivity

    @ContributesAndroidInjector
    abstract fun bindTabExecuteFragment(): TabExecuteFragment

    @ContributesAndroidInjector
    abstract fun bindTabAllFragment(): TabAllFragment

    @ContributesAndroidInjector
    abstract fun bindTabEssentialFragment(): TabEssentialFragment

    @ContributesAndroidInjector
    abstract fun bindTabUpdateFragment(): TabUpdateFragment

    @ContributesAndroidInjector
    abstract fun bindDetailActivity(): DetailActivity

    @ContributesAndroidInjector
    abstract fun bindNoticeActivity(): NoticeActivity

    @ContributesAndroidInjector
    abstract fun bindLockScreenActivity(): LockScreenActivity

    @ContributesAndroidInjector
    abstract fun bindErrorLogActivity(): ErrorLogActivity

    @ContributesAndroidInjector
    abstract fun bindSettingActivity(): SettingActivity

    @ContributesAndroidInjector
    abstract fun bindSettingPushReceiver(): PushReceiver

}