package io.julius.chow.data

import android.annotation.SuppressLint
import android.util.Log
import io.julius.chow.data.mapper.*
import io.julius.chow.data.source.DataSource
import io.julius.chow.data.source.DataSourceQualifier
import io.julius.chow.data.source.Source
import io.julius.chow.domain.ChowRepository
import io.julius.chow.domain.Exception
import io.julius.chow.domain.Result
import io.julius.chow.domain.model.*
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides an implementation of the [ChowRepository] interface for communicating to and from
 * Data sources
 */
@Singleton
@SuppressLint("CheckResult")
class ChowRepositoryImpl @Inject constructor(
    @DataSourceQualifier(Source.Local)
    private val localDataSource: DataSource,
    @DataSourceQualifier(Source.Remote)
    private val remoteDataSource: DataSource
) : ChowRepository {

    override fun isUserLoggedIn(): Result<Boolean> {
        return localDataSource.isUserLoggedIn()
    }

    override suspend fun authenticateUser(): Result<UserModel> {
        return remoteDataSource.authenticateUser()
    }

    override suspend fun getCurrentUser(): Flowable<Result<UserModel>> {
        return localDataSource.getCurrentUser().map {
            when (it) {
                is Result.Success -> {
                    Result.Success(UserEntityMapper.mapFromEntity(it.data))
                }
                is Result.Failure -> {
                    Result.Failure(Exception.LocalDataNotFoundException)
                }
            }
        }
    }

    override suspend fun fetchCurrentUser(): UserModel {
        val user = localDataSource.fetchCurrentUser()
        return UserEntityMapper.mapFromEntity(user)
    }

    override suspend fun saveUser(userModel: UserModel): Result<Boolean> {
        remoteDataSource.saveUser(UserEntityMapper.mapToEntity(userModel))
        return saveUserLocally(userModel)
    }

    override suspend fun saveUserLocally(userModel: UserModel): Result<Boolean> {
        return localDataSource.saveUser(UserEntityMapper.mapToEntity(userModel))
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

    override suspend fun getMenu(category: String): Flowable<Result<List<FoodModel>>> {
        remoteDataSource.getMenu(category).subscribe {
            when (it) {
                is Result.Success -> {
                    localDataSource.saveFood(it.data)
                    Log.e("CHOW", "Remote success: ${it.data.size}")
                }

                is Result.Failure -> Result.Failure(Exception.RemoteDataNotFoundException)
            }
        }

        return localDataSource.getMenu(category).map {
            when (it) {
                is Result.Success -> {
                    // Map each data entity to a domain model
                    val menu = it.data.map { foodEntity -> FoodEntityMapper.mapFromEntity(foodEntity) }
                    Log.e("CHOW", "Local success: ${it.data.size}")
                    Result.Success(menu)
                }

                is Result.Failure -> {
                    Result.Failure(Exception.LocalDataNotFoundException)
                }
            }
        }
    }

    override suspend fun getOrders(): Flowable<Result<List<OrderModel>>> {
        // Return local database result
        return localDataSource.getOrders()
            .map {
                when (it) {
                    is Result.Success -> {
                        // Map each data layer entity to a domain layer model
                        val orders = it.data.map { orderEntity ->
                            OrderEntityMapper.mapFromEntity(orderEntity)
                        }

                        Result.Success(orders)
                    }
                    is Result.Failure -> {
                        Result.Failure(Exception.LocalDataNotFoundException)
                    }
                }
            }
    }

    override suspend fun getOrder(id: String): OrderModel? {
        val orderEntity = localDataSource.getOrder(id) ?: return null
        return OrderEntityMapper.mapFromEntity(orderEntity)
    }

    override suspend fun saveOrder(orderModel: OrderModel): Boolean {
        val orderEntity = OrderEntityMapper.mapToEntity(orderModel)
        return localDataSource.saveOrder(orderEntity)
    }

    override suspend fun deleteOrder(orderModel: OrderModel) {
        localDataSource.deleteOrder(OrderEntityMapper.mapToEntity(orderModel))
    }

    override suspend fun placeOrder(placedOrder: PlacedOrderModel): Result<String> {

        return when (val response = remoteDataSource.placeOrder(PlacedOrderEntityMapper.mapToEntity(placedOrder))) {
            is Result.Success -> {
                // Save to local datastore and remove items from Orders db table
                val isCached = localDataSource.savePlacedOrder(response.data)
                if (isCached) {
                    Result.Success("Your order was placed successfully.")
                } else {
                    Result.Failure(Exception.LocalDataException("Could not cached data locally."))
                }
            }

            is Result.Failure -> {
                response
            }
        }
    }
}