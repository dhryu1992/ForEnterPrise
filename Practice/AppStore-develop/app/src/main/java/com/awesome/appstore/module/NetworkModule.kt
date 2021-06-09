package com.awesome.appstore.module

import com.awesome.appstore.AppStoreApplication
import com.awesome.appstore.BuildConfig
import com.awesome.appstore.config.StoreConfig
import com.awesome.appstore.network.NetworkService
import com.awesome.appstore.util.StorePreference
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class NetworkModule(private val application: AppStoreApplication?){

    val CONNECT_TIMEOUT = 15
    val WRITE_TIMEOUT = 15
    val READ_TIMEOUT = 15

    @Provides
    @Singleton
    fun provideOkHttpCache(): Cache? {
        val cacheSize = 10 * 1024 * 1024 // 10MB
        return Cache(application!!.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideInterceptor(preference: StorePreference): Interceptor {
        return Interceptor { chain ->
            if (preference.loadTokenPreference()?.isNotEmpty()!!) {
                val token: String? = preference.loadTokenPreference()
                val request = chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .header("Accept", "application/json")
                        .build()
                Logger.d(request.headers())
                chain.proceed(request)
            } else {
                chain.proceed(chain.request())
            }
        }
    }

    @Provides
    @Singleton
    fun HttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message -> Logger.d("Message$message") }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache?,
        interceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            if (BuildConfig.IS_MOCK){
                return OkHttpClient.Builder()
                    .cache(cache)
                    .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .addNetworkInterceptor(httpLoggingInterceptor)
                    .build()
            }
            OkHttpClient.Builder()
                    .cache(cache)
//                    .protocols(Arrays.asList(Protocol.H2_PRIOR_KNOWLEDGE))
                    .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .addNetworkInterceptor(httpLoggingInterceptor)
                    .build()
        } else {
            OkHttpClient.Builder()
                    .cache(cache)
//                    .protocols(Arrays.asList(Protocol.H2_PRIOR_KNOWLEDGE))
                    .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build()
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): NetworkService {
        if (BuildConfig.IS_MOCK){
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(StoreConfig.MOCK_API)
                .client(okHttpClient)
                .build()
            return retrofit.create(NetworkService::class.java)
        }

        val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(StoreConfig.BASEURL)
                .client(okHttpClient)
                .build()
        return retrofit.create(NetworkService::class.java)
    }
}