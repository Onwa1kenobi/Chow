package io.julius.chow.data.source

import io.julius.chow.data.model.FoodEntity
import io.julius.chow.data.model.OrderEntity
import io.julius.chow.data.model.RestaurantEntity
import io.julius.chow.data.model.UserEntity
import io.julius.chow.domain.Exception
import io.julius.chow.domain.Result
import io.julius.chow.domain.model.UserModel
import io.reactivex.Flowable

/**
 * Interface defining methods for the data storage and retrieval operations related to Present.
 * This is to be implemented by external data source layers (Remote and Cache), setting the requirements for the
 * operations that need to be implemented.
 */
interface DataSource {

    /**
     * Check if a user is currently logged in.
     * With default implementation since we only want to use this from LocalDataSource
     */
    fun isUserLoggedIn(): Result<Boolean> {
        return Result.Success(false)
    }

    /**
     * Check if a user is currently logged in.
     * With default implementation since we only want to use this from RemoteDataSource
     */
    suspend fun authenticateUser(): Result<UserModel> {
        return Result.Failure(Exception.Error)
    }

    /**
     * Fetch the current user the database
     */
    suspend fun getCurrentUser(): Flowable<Result<UserEntity>> {
        TODO("Not Implemented")
    }

    /**
     * Saves the complete user profile to the database
     */
    suspend fun saveUser(userEntity: UserEntity): Result<Boolean> {
        return Result.Success(false)
    }

    /**
     * Gets all the available restaurants from the local database and the remote database
     */
    suspend fun fetchRestaurants(): Flowable<Result<List<RestaurantEntity>>>

    /**
     * Saves a restaurant to the local database
     */
    fun saveRestaurants(restaurantEntities: List<RestaurantEntity>) {}

    /**
     * Gets all the food offered by the restaurant with whose id was passed from the local database and the remote database
     */
    suspend fun fetchRestaurantMenu(restaurantId: String): Flowable<Result<List<FoodEntity>>>

    /**
     * Saves a food to the local database
     */
    fun saveFood(foodEntities: List<FoodEntity>) {}

    /**
     * Gets all the saved orders from the local database
     */
    suspend fun getOrders(): Flowable<Result<List<OrderEntity>>> {
        TODO("Not Implemented")
    }

    /**
     * Gets an order with the id passed
     * We don't want to implement this in our remote data source, so we give it an initialization and return null
     */
    fun getOrder(id: String): OrderEntity? {
        return null
    }

    /**
     * Saves an order to the local database
     */
    fun saveOrder(orderEntity: OrderEntity): Boolean {
        return false
    }

    /**
     * Deletes an order from the local database
     */
    fun deleteOrder(orderEntity: OrderEntity) {}
}