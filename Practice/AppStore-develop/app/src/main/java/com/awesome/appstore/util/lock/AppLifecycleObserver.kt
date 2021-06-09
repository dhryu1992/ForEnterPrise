package com.awesome.appstore.util.lock

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.awesome.appstore.activity.ScreenLockActivity
import com.awesome.appstore.util.StorePreference
import com.orhanobut.logger.Logger

class AppLifecycleObserver(
    private val context: Context
) : LifecycleObserver {
    val storePreference = StorePreference(context)
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        Logger.d("onForeground")
        Logger.d(storePreference.loadIdSavePreference())
        val lockSetting=storePreference.loadLockScreenSetting()
        if (lockSetting.equals("1")&& storePreference.loadIdSavePreference()?.isNotEmpty()!!) ScreenLockActivity.start(context, false, null)
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        Logger.d("onBackground")
        //background
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Logger.d("onDestroy")
        Logger.d(storePreference.loadTokenPreference())
        Logger.d(storePreference.loadIdSavePreference())
        storePreference.saveTokenPreference("")
        storePreference.saveIdSavePreference("")
        Logger.d(storePreference.loadTokenPreference())
        Logger.d(storePreference.loadIdSavePreference())
    }

}
