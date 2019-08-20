package io.julius.chow.domain.repository

import io.julius.chow.domain.Result
import io.julius.chow.domain.model.*
import io.reactivex.Flowable

/**
 * Interface defining methods for how the Domain layer communicates with the Data layer.
 * This is to be implemented by the Data layer, setting the requirements for the
 * operations that need to be implemented.
 */
interface ChowRepository {

    fun isUserLoggedIn(): Result<Boolean>

    fun getCurrentLoggedAccount(): Any?

    fun getCurrentLoggedAccountType(): Result<UserType>

    suspend fun authenticateUser(): Result<UserModel>

    suspend fun getCurrentUser(): Flowable<Result<UserModel>>

    suspend fun fetchCurrentUser(): UserModel

    suspend fun saveUser(userModel: UserModel): Result<Boolean>

    suspend fun saveUserLocally(userModel: UserModel): Result<Boolean>

    suspend fun fetchRestaurants(): Flowable<Result<List<RestaurantModel>>>

    suspend fun getMenu(category: String): Flowable<Result<List<FoodModel>>>

    suspend fun getOrders(): Flowable<Result<List<OrderModel>>>

    suspend fun getOrder(id: String): OrderModel?

    suspend fun saveOrder(orderModel: OrderModel): Boolean

    suspend fun deleteOrder(orderModel: OrderModel)

    suspend fun placeOrder(placedOrder: PlacedOrderModel): Result<String>
}