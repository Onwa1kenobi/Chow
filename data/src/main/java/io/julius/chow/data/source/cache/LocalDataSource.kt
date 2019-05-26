package io.julius.chow.data.source.cache

import com.google.firebase.auth.FirebaseAuth
import io.julius.chow.data.model.FoodEntity
import io.julius.chow.data.model.OrderEntity
import io.julius.chow.data.model.RestaurantEntity
import io.julius.chow.data.model.UserEntity
import io.julius.chow.data.source.DataSource
import io.julius.chow.domain.Exception
import io.julius.chow.domain.Result
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val appDAO: AppDAO) : DataSource {

    override fun isUserLoggedIn(): Result<Boolean> {
        // Check if a user is currently signed in
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        return if (currentUser == null) {
            // No logged in user
            Result.Success(false)
        } else {
            // User is logged in

            val user: UserEntity? = appDAO.getUser(currentUser.uid)
            if (user != null && user.profileComplete) {
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        }
    }

    override suspend fun getCurrentUser(): Flowable<Result<UserEntity>> {
        return Flowable.create({
            appDAO.getCurrentUser().subscribe { userEntity ->
                if (userEntity == null) {
                    it.onError(Exception.LocalDataNotFoundException)
                } else {
                    it.onNext(Result.Success(userEntity))
                }
            }
        }, BackpressureStrategy.LATEST)
    }

    override suspend fun fetchRestaurants(): Flowable<Result<List<RestaurantEntity>>> =
    // Since we can't get Room to return our custom Result type, we get the normal data, and create a Flowable
        // to which we pass a successful list of data objects or an error and propagate down to the subscriber.
        Flowable.create({
            appDAO.getRestaurants().subscribe { restaurants ->
                if (restaurants == null) {
                    it.onError(Exception.LocalDataNotFoundException)
                } else {
                    it.onNext(Result.Success(restaurants))
                }
            }
        }, BackpressureStrategy.BUFFER)

    override suspend fun saveUser(userEntity: UserEntity): Result<Boolean> {
        val rowId: Long? = appDAO.saveUser(userEntity)
        return if (rowId == null) {
            Result.Failure(Exception.LocalDataException("Failed to save user"))
        } else {
            Result.Success(true)
        }
    }

    override fun saveRestaurants(restaurantEntities: List<RestaurantEntity>) {
        restaurantEntities.forEach { appDAO.saveRestaurant(it) }
    }

    override suspend fun fetchRestaurantMenu(restaurantId: String): Flowable<Result<List<FoodEntity>>> =
    // Since we can't get Room to return our custom Result type, we get the normal data, and create a Flowable
        // to which we pass a successful list of data objects or an error and propagate down to the subscriber.
        Flowable.create({
            appDAO.getRestaurantMenu(restaurantId).subscribe { menu ->
                if (menu == null) {
                    it.onError(Exception.LocalDataNotFoundException)
                } else {
                    it.onNext(Result.Success(menu))
                }
            }
        }, BackpressureStrategy.BUFFER)

    override fun saveFood(foodEntities: List<FoodEntity>) {
        foodEntities.forEach { appDAO.saveFood(it) }
    }

    override suspend fun getOrders(): Flowable<Result<List<OrderEntity>>> {
        return Flowable.create({
            appDAO.getOrders().subscribe { orders ->
                if (orders == null) {
                    it.onError(Exception.LocalDataNotFoundException)
                } else {
                    it.onNext(Result.Success(orders))
                }
            }
        }, BackpressureStrategy.BUFFER)
    }

    override fun getOrder(id: String): OrderEntity? {
        return appDAO.getOrder(id)
    }

    override fun saveOrder(orderEntity: OrderEntity): Boolean {
        val rowId: Long? = appDAO.saveOrder(orderEntity)
        return rowId != null
    }

    override fun deleteOrder(orderEntity: OrderEntity) {
        appDAO.deleteOrder(orderEntity)
    }
}