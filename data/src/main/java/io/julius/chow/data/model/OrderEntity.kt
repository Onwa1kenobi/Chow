package io.julius.chow.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Orders")
class OrderEntity() {

    @PrimaryKey
    lateinit var id: String
    lateinit var restaurantId: String
    lateinit var title: String
    lateinit var imageUrl: String
    lateinit var description: String
    var price: Double = 0.0
    var rating: Double = 0.0
    var quantity: Int = 1
    // Boolean variable to check if an order has been processed or not.
    var inHistory: Boolean = false

    @Ignore
    constructor(
        id: String,
        title: String,
        imageUrl: String,
        description: String,
        restaurantId: String,
        price: Double,
        rating: Double,
        quantity: Int,
        inHistory: Boolean = false
    ) : this() {
        this.id = id
        this.title = title
        this.imageUrl = imageUrl
        this.description = description
        this.restaurantId = restaurantId
        this.price = price
        this.rating = rating
        this.quantity = quantity
        this.inHistory = inHistory
    }
}