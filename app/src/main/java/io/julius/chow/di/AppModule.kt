package io.julius.chow.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.julius.chow.app.App
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    @Named("AppContext")
    fun provideApplicationContext(): Context = app.applicationContext

}