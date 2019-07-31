package io.julius.chow.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.julius.chow.data.repository.ChowRepositoryImpl
import io.julius.chow.data.repository.RestaurantRepositoryImpl
import io.julius.chow.data.source.DataSource
import io.julius.chow.data.source.DataSourceQualifier
import io.julius.chow.data.source.Source
import io.julius.chow.data.source.cache.AppDAO
import io.julius.chow.data.source.cache.AppDatabase
import io.julius.chow.data.source.cache.LocalDataSource
import io.julius.chow.data.source.remote.RemoteDataSource
import io.julius.chow.domain.repository.ChowRepository
import io.julius.chow.domain.repository.RestaurantRepository
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [AppModule::class])
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@Named("AppContext") appContext: Context): AppDatabase = AppDatabase.getInstance(appContext)

    @Provides
    @Singleton
    fun provideAppDao(appDatabase: AppDatabase): AppDAO = appDatabase.appDao()

    @Provides
    @Singleton
    fun provideChowRepository(repository: ChowRepositoryImpl): ChowRepository = repository

    @Provides
    @Singleton
    fun provideRestaurantRepository(repository: RestaurantRepositoryImpl): RestaurantRepository = repository

    @Provides
    @DataSourceQualifier(Source.Local)
    fun provideLocalDataSource(dataSource: LocalDataSource): DataSource = dataSource

    @Provides
    @DataSourceQualifier(Source.Remote)
    fun provideRemoteDataSource(dataSource: RemoteDataSource): DataSource = dataSource
}