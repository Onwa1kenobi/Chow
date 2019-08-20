package io.julius.chow.data.source

import io.julius.chow.data.model.*
import io.julius.chow.domain.Exception
import io.julius.chow.domain.Result
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.domain.model.UserModel
import io.julius.chow.domain.model.UserType
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
     * Get the currently logged in account.
     * With default implementation since we only want to use this from LocalDataSource
     */
    fun getCurrentLoggedAccount(): Any? {
        return Result.Success(false)
    }

    /**
     * Get the currently logged in account type.
     * With default implementation since we only want to use this from LocalDataSource
     */
    fun getCurrentLoggedAccountType(): Result<UserType> {
        return Result.Failure(Exception.NotImplementedException)
    }

    /**
     * Check if a user is currently logged in.
     * With default implementation since we only want to use this from RemoteDataSource
     */
    suspend fun authenticateUser(): Result<UserModel> {
        return Result.Failure(Exception.NotImplementedException)
    }

    /**
     * Fetch the current user from the local database
     */
    suspend fun getCurrentUser(): Flowable<Result<UserEntity>> {
        TODO("Not Implemented")
    }

    /**
     * Single fetch operation to get the current user from the local database
     */
    suspend fun fetchCurrentUser(): UserEntity {
        TODO("Not Implemented")
    }

    /**
     * Saves the complete user profile to the database
     */
    suspend fun saveUser(userEntity: UserEntity): Result<Boolean> {
        return Result.Success(false)
    }

    /**
     * Check if a restaurant is currently logged in.
     * With default implementation since we only want to use this from RemoteDataSource
     */
    suspend fun authenticateRestaurant(): Result<RestaurantModel> {
        return Result.Failure(Exception.Error)
    }

    /**
     * Fetch the current restaurant from the local database
     */
    suspend fun getCurrentRestaurant(): Flowable<Result<RestaurantEntity>> {
        TODO("Not Implemented")
    }

    /**
     * Single fetch operation to get the current user from the local database
     */
    suspend fun fetchCurrentRestaurant(): RestaurantEntity {
        TODO("Not Implemented")
    }

    /**
     * Saves the complete restaurant profile to the database
     */
    suspend fun saveRestaurant(restaurantEntity: RestaurantEntity): Result<Boolean> {
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
     * Gets all the food related to the category passed
     */
    suspend fun getMenu(category: String): Flowable<Result<List<FoodEntity>>>

    /**
     * Saves a list of food items to the local database
     */
    fun saveFood(foodEntities: List<FoodEntity>) {}

    /**
     * Saves a food to the database
     */
    suspend fun saveFood(foodEntity: FoodEntity): Result<FoodEntity> {
        return Result.Failure(Exception.NotImplementedException)
    }

    /**
     * Gets all the saved orders from the local database
     */
    suspend fun getOrders(): Flowable<Result<List<OrderEntity>>> {
        TODO("Not Implemented")
    }

    /**
     * Gets all the orders to be processed by the restaurant with id passed
     */
    suspend fun getRestaurantOrders(restaurantId: String): Flowable<Result<List<OrderEntity>>>

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
     * Saves a list of orders to the local database
     */
    fun saveOrders(orderEntity: List<OrderEntity>): Boolean {
        return false
    }

    /**
     * Deletes an order from the local database
     */
    fun deleteOrder(orderEntity: OrderEntity) {}

    /**
     * Places an order and sends a notification to corresponding restaurants from the remote database
     */
    suspend fun placeOrder(placedOrder: PlacedOrderEntity): Result<PlacedOrderEntity> {
        return Result.Failure(Exception.NotImplementedException)
    }

    /**
     * Saves a placed order in the local database for posterity sake
     */
    suspend fun savePlacedOrder(placedOrder: PlacedOrderEntity): Boolean {
        return false
    }
}