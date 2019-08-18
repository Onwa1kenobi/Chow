package io.julius.chow.domain.repository

import io.julius.chow.domain.Result
import io.julius.chow.domain.model.FoodModel
import io.julius.chow.domain.model.RestaurantModel
import io.reactivex.Flowable

/**
 * Interface defining methods for how the Domain layer communicates with the Data layer.
 * This is to be implemented by the Data layer, setting the requirements for the
 * operations that need to be implemented.
 */
interface RestaurantRepository {

    suspend fun authenticateRestaurant(): Result<RestaurantModel>

    suspend fun getCurrentRestaurant(): Flowable<Result<RestaurantModel>>

    suspend fun fetchCurrentRestaurant(): RestaurantModel

    suspend fun saveRestaurant(restaurantModel: RestaurantModel): Result<Boolean>

    suspend fun saveRestaurantLocally(restaurantModel: RestaurantModel): Result<Boolean>

    suspend fun fetchRestaurants(): Flowable<Result<List<RestaurantModel>>>

    suspend fun fetchRestaurantMenu(restaurantId: String): Flowable<Result<List<FoodModel>>>

    suspend fun saveFood(foodModel: FoodModel): Result<Any>
}