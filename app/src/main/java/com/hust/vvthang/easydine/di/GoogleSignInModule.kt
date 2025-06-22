package com.module.core.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GoogleSignInModule {

    companion object {
        @Provides
        @Singleton
        fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("355702919962-avftoucolas8hb8nnp609h4go66ul9cm.apps.googleusercontent.com")
                .requestEmail()
                .build()
            return GoogleSignIn.getClient(context, gso)
        }
    }
}