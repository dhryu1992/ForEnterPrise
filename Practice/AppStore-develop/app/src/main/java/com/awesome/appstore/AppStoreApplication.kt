package com.awesome.appstore

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.awesome.appstore.util.lock.AppLifecycleObserver
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.facebook.stetho.Stetho
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraToast
import org.acra.data.StringFormat

@AcraCore(buildConfigClass = BuildConfig::class, reportFormat = StringFormat.JSON)
//@AcraToast(resText = R.string.error_toast, length = Toast.LENGTH_SHORT)
class AppStoreApplication : DaggerApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Stetho.initializeWithDefaults(this)
        Logger.addLogAdapter(AndroidLogAdapter())
        ProcessLifecycleOwner.get().lifecycle
            .addObserver(AppLifecycleObserver(applicationContext))
        PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build();
        PRDownloader.initialize(applicationContext);
    }

    override fun applicationInjector(): AndroidInjector<out AppStoreApplication?>? {
        return DaggerAppComponent.builder().create(this)
    }

}