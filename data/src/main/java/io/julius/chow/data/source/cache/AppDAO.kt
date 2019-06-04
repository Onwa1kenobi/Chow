package io.julius.chow.data.source.cache

import androidx.room.*
import io.julius.chow.data.model.FoodEntity
import io.julius.chow.data.model.OrderEntity
import io.julius.chow.data.model.RestaurantEntity
import io.julius.chow.data.model.UserEntity
import io.reactivex.Flowable

@Dao
interface AppDAO {

    @Query("SELECT * FROM User")
    fun getCurrentUser(): Flowable<UserEntity>

    @Query("SELECT * FROM User")
    fun fetchCurrentUser(): UserEntity

    @Query("SELECT * FROM User where id = :id")
    fun getUser(id: String): UserEntity

    @Query("SELECT * FROM Restaurants")
    fun getRestaurants(): Flowable<List<RestaurantEntity>>

    @Query("SELECT * FROM Food where restaurantId = :id")
    fun getRestaurantMenu(id: String): Flowable<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(userEntity: UserEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRestaurant(restaurantEntity: RestaurantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFood(foodEntity: FoodEntity)

    @Query("DELETE FROM User")
    fun deleteUser()

    @Query("DELETE FROM Restaurants")
    fun deleteRestaurants()

    @Query("SELECT * FROM Orders")
    fun getOrders() : Flowable<List<OrderEntity>>

    @Query("SELECT * FROM Orders where id = :id")
    fun getOrder(id: String) : OrderEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOrder(orderEntity: OrderEntity) : Long

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)
}