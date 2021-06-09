package com.awesomebly.template.android

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.awesomebly.template.android.util.observer.AppLifecycleObserver
import com.facebook.stetho.Stetho
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : TpApplication
 * Date : 2021-04-30, 오후 4:58
 * History
seq   date          contents      programmer
01.   2021-04-30    init            차태준
02.
03.
 */

@HiltAndroidApp
class TpApplication : Application() { // Hilt 사용시 선행되어야 할 부분. 설정방법은 HiltAndroid 어노트이션을 Application 클래스에 추가히기만 하면 됨.

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
        Stetho.initializeWithDefaults(this)
        ProcessLifecycleOwner.get().lifecycle
            .addObserver(AppLifecycleObserver(this))
    }

}