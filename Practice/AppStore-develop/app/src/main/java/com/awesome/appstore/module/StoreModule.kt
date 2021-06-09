package com.awesome.appstore.module

import android.content.Context
import android.os.Environment
import com.awesome.appstore.AppStoreApplication
import com.awesome.appstore.database.DatabaseHandler
import com.awesome.appstore.database.StoreDatabase
import com.awesome.appstore.network.NetworkService
import com.awesome.appstore.network.NetworkHandler
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.FileDirectoryInfo
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.awesome.appstore.util.lock.LockManager
import com.google.gson.Gson
import com.scottyab.rootbeer.RootBeer
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
class StoreModule(private val application: AppStoreApplication?) {

    @Provides
    @Singleton
    fun providePreference(): StorePreference = StorePreference(application!!.applicationContext)

    @Provides
    @Singleton
    fun provideAppDatabase(): StoreDatabase {
        return StoreDatabase.getInstance(application!!.applicationContext)
    }

    @Provides
    @Singleton
    fun provideExecutorService(): ExecutorService {
        val threadId = AtomicInteger()
        val defaultParallelism = (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1)
        return Executors.newFixedThreadPool(defaultParallelism) {
            Thread(it, "CommonPool-worker-${threadId.incrementAndGet()}").apply { isDaemon = true }
        }
    }

    @Provides
    @Singleton
    fun provideDispatcher(executorService: ExecutorService): ExecutorCoroutineDispatcher {
        return executorService.asCoroutineDispatcher();
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideResponseHandler(): NetworkHandler {
        return NetworkHandler()
    }

    @Provides
    @Singleton
    fun provideDataBaseHandler(): DatabaseHandler {
        return DatabaseHandler()
    }

    fun provideDatabaseHandler(): DatabaseHandler {
        return DatabaseHandler()
    }

    @Provides
    @Singleton
    fun providesRepository(
        networkService: NetworkService,
        storeDatabase: StoreDatabase,
        networkHandler: NetworkHandler,
        databaseHandler: DatabaseHandler,
    ): Repository {
        return Repository(networkService, storeDatabase, networkHandler, databaseHandler)
    }

    @Provides
    @Singleton
    fun providePackageUtil(): PackageUtil {
        return PackageUtil(application!!.applicationContext)
    }

    @Provides
    @Singleton
    fun provideLockManager(): LockManager {
        return LockManager(application!!.applicationContext)
    }
    @Provides
    @Singleton
    fun provideRootBeer(): RootBeer {
        return RootBeer(application!!.applicationContext)
    }

    @Provides
    @Singleton
    fun provideFileDirectoryInfo(): FileDirectoryInfo {

        val rootDirectoryInfo = Environment.getRootDirectory().toString()
            .toString()
        val externalDownLoads =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

        val internalDownLoads = application!!.applicationContext.filesDir.absolutePath+"/${Environment.DIRECTORY_DOWNLOADS}"

        val caches =
            application.applicationContext.externalCacheDir.toString()
                .toString()
        val pictures =
            application.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString()
        return FileDirectoryInfo(rootDirectoryInfo,
            externalDownLoads,
            internalDownLoads,
            caches,
            pictures
        )
    }

}