package com.awesomebly.template.android.util.observer

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : AppLifecycleObserver
 * Date : 2021-04-30, 오후 5:10
 * History
seq   date          contents      programmer
01.   2021-04-30    init            차태준
02.
03.
 */

class AppLifecycleObserver (val context: Context) : LifecycleObserver {

    /**
     * On foreground
     *  어플리케이션 포그라운드로 진입시 호출
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        Logger.d("Application onForeground")
    }

    /**
     * On background
     * 어플리케이션 백그라운드로 진입시 호출
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        Logger.d("Application onBackground")
    }

}