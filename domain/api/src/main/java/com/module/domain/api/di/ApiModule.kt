package com.module.domain.api.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.module.core.network.interceptor.NetworkInterceptor
import com.module.core.utils.extensions.constants.Constants
import com.module.domain.api.BuildConfig
import com.module.domain.api.interfaces.AuthApiInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class AuthHttpClient

    @Provides
    @Singleton
    @AuthHttpClient
    fun provideAuthApiClient(networkInterceptor: NetworkInterceptor, ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().run {
            readTimeout(Constants.READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            connectTimeout(Constants.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
                addInterceptor(loggingInterceptor)
            }
            addInterceptor(networkInterceptor)
            build()
        }
    }

    @Provides
    @Singleton
    fun provideAuthApiInterface(
        @AuthHttpClient authClient: OkHttpClient,
        gson: Gson,
    ): AuthApiInterface {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(AuthApiInterface::class.java)
    }
}