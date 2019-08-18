package io.julius.chow.data.repository

import android.annotation.SuppressLint
import io.julius.chow.data.mapper.FoodEntityMapper
import io.julius.chow.data.mapper.RestaurantEntityMapper
import io.julius.chow.data.source.DataSource
import io.julius.chow.data.source.DataSourceQualifier
import io.julius.chow.data.source.Source
import io.julius.chow.domain.Exception
import io.julius.chow.domain.Result
import io.julius.chow.domain.model.FoodModel
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.domain.repository.ChowRepository
import io.julius.chow.domain.repository.RestaurantRepository
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides an implementation of the [ChowRepository] interface for communicating to and from
 * Data sources
 */
@Singleton
@SuppressLint("CheckResult")
class RestaurantRepositoryImpl @Inject constructor(
    @DataSourceQualifier(Source.Local)
    private val localDataSource: DataSource,
    @DataSourceQualifier(Source.Remote)
    private val remoteDataSource: DataSource
) : RestaurantRepository {

    override suspend fun authenticateRestaurant(): Result<RestaurantModel> {
        return remoteDataSource.authenticateRestaurant()
    }

    override suspend fun getCurrentRestaurant(): Flowable<Result<RestaurantModel>> {
        return localDataSource.getCurrentRestaurant().map {
            when (it) {
                is Result.Success -> {
                    Result.Success(RestaurantEntityMapper.mapFromEntity(it.data))
                }
                is Result.Failure -> {
                    Result.Failure(Exception.LocalDataNotFoundException)
                }
            }
        }
    }

    override suspend fun fetchCurrentRestaurant(): RestaurantModel {
        val restaurant = localDataSource.fetchCurrentRestaurant()
        return RestaurantEntityMapper.mapFromEntity(restaurant)
    }

    override suspend fun saveRestaurant(restaurantModel: RestaurantModel): Result<Boolean> {
        remoteDataSource.saveRestaurant(RestaurantEntityMapper.mapToEntity(restaurantModel))
        return saveRestaurantLocally(restaurantModel)
    }

    override suspend fun saveRestaurantLocally(restaurantModel: RestaurantModel): Result<Boolean> {
        return localDataSource.saveRestaurant(RestaurantEntityMapper.mapToEntity(restaurantModel))
    }

    override suspend fun fetchRestaurants(): Flowable<Result<List<RestaurantModel>>> {
        // Fetch remote data and save to local storage
        remoteDataSource.fetchRestaurants().subscribe {
            when (it) {
                is Result.Success -> {
                    localDataSource.saveRestaurants(it.data)
                }

                is Result.Failure -> {
                    Result.Failure(Exception.LocalDataNotFoundException)
                }
            }
        }

        // Return local database result
        return localDataSource.fetchRestaurants()
            .map {
                when (it) {
                    is Result.Success -> {
                        // Map each restaurant entity to a restaurant model
                        val restaurants = it.data.map { restaurantEntity ->
                            RestaurantEntityMapper.mapFromEntity(restaurantEntity)
                        }

                        Result.Success(restaurants)
                    }
                    is Result.Failure -> {
                        Result.Failure(Exception.LocalDataNotFoundException)
                    }
                }
            }
    }

    override suspend fun fetchRestaurantMenu(restaurantId: String): Flowable<Result<List<FoodModel>>> {
        // Fetch remote data and save to local storage
        remoteDataSource.fetchRestaurantMenu(restaurantId).subscribe {
            when (it) {
                is Result.Success -> {
                    localDataSource.saveFood(it.data)
                }

                is Result.Failure -> {
                    Result.Failure(Exception.LocalDataNotFoundException)
                }
            }
        }

        // Return local database result
        return localDataSource.fetchRestaurantMenu(restaurantId).map {
            when (it) {
                is Result.Success -> {
                    // Map each data entity to a domain model
                    val menu = it.data.map { foodEntity -> FoodEntityMapper.mapFromEntity(foodEntity) }
                    Result.Success(menu)
                }

                is Result.Failure -> {
                    Result.Failure(Exception.LocalDataNotFoundException)
                }
            }
        }
    }

    override suspend fun saveFood(foodModel: FoodModel): Result<Any> {
        val remoteResponse = remoteDataSource.saveFood(FoodEntityMapper.mapToEntity(foodModel))
        if (remoteResponse is Result.Failure) {
            return remoteResponse
        }

        val saveStatus = localDataSource.saveFood((remoteResponse as Result.Success).data)
        return if (saveStatus is Result.Success) {
            Result.Success(true)
        } else {
            saveStatus
        }
    }
}