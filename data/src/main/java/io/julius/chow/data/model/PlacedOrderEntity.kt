package io.julius.chow.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "PlacedOrders")
data class PlacedOrderEntity(val version: Int = 1) {

    @PrimaryKey
    lateinit var id: String
    lateinit var orders: List<OrderEntity>
    @Embedded(prefix = "user")
    lateinit var user: UserEntity
    lateinit var createdAt: Date
    lateinit var deliveryTime: String
    var subTotalCost: Double = 0.0
    var tax: Double = 0.0
    var deliveryCharge: Double = 0.0
    lateinit var restaurantIds: List<String>

    @Ignore
    constructor(
        id: String,
        orders: List<OrderEntity>,
        user: UserEntity,
        createdAt: Date,
        deliveryTime: String,
        subTotalCost: Double,
        tax: Double,
        deliveryCharge: Double,
        restaurantIds: List<String>
    ) : this() {
        this.id = id
        this.orders = orders
        this.user = user
        this.createdAt = createdAt
        this.deliveryTime = deliveryTime
        this.subTotalCost = subTotalCost
        this.tax = tax
        this.deliveryCharge = deliveryCharge
        this.restaurantIds = restaurantIds
    }
}