package com.awesomebly.template.android.module

import android.annotation.SuppressLint
import android.content.Context
import com.awesomebly.template.android.network.api.PostsApi
import com.awesomebly.template.android.network.handler.NetworkHandler
import com.awesomebly.template.android.util.TpPreference
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.BASE_URL
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.CACHE_SIZE
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.OK_HTTP_CONNECTION_COUNT
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.OK_HTTP_CONNECTION_DURATION
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.OK_HTTP_CONNECTION_TIMEOUT
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.OK_HTTP_READ_TIMEOUT
import com.awesomebly.template.android.util.config.Constants.Companion.Setting.OK_HTTP_WRITE_TIMEOUT
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : NetworkModule
 * Date : 2021-05-03, 오후 3:38
 * History
seq   date          contents      programmer
01.   2021-05-03                   차태준
02.
03.
 */
@InstallIn(SingletonComponent::class) 
@Module
object NetworkModule {

    @Provides
    fun provideNetworkHandler(): NetworkHandler {
        return NetworkHandler()
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(appContext: Context): Cache {
        return Cache(appContext.cacheDir, CACHE_SIZE.toLong())
    }

    @Provides
    @Singleton
    fun provideInterceptor(preference: TpPreference): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                // 헤더에 인증값이나 로케일을 입력하는 등 인터셉터에서 필요한 작업 기입
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideHttpConnectionPool(): ConnectionPool {
        return ConnectionPool(
            OK_HTTP_CONNECTION_COUNT,
            OK_HTTP_CONNECTION_DURATION,
            TimeUnit.MINUTES
        )
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message -> Logger.d("LoggingInterceptor Message\n$message") }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
//        확인하려는 정보에 따라 인터셉터 레벨 변경하여 사용
//        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        interceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        provideHttpConnectionPool: ConnectionPool
    ): OkHttpClient {
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )

        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }

        return builder
            .cache(cache)
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectTimeout(OK_HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(OK_HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(OK_HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .connectionPool(provideHttpConnectionPool)
            .addInterceptor(interceptor)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providePostsApi(retrofit: Retrofit): PostsApi {
        return retrofit.create(PostsApi::class.java)
    }
}