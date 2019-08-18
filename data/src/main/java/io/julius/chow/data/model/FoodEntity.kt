package io.julius.chow.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Food")
data class FoodEntity(val version: Int = 1) {

    @PrimaryKey
    lateinit var id: String
    lateinit var restaurantId: String
    lateinit var title: String
    lateinit var imageUrl: String
    lateinit var description: String
    lateinit var category: String
    var price: Double = 0.0
    var rating: Double = 0.0

    @Ignore
    constructor(
        id: String,
        title: String,
        imageUrl: String,
        description: String,
        restaurantId: String,
        category: String,
        price: Double,
        rating: Double
    ) : this() {
        this.id = id
        this.title = title
        this.imageUrl = imageUrl
        this.description = description
        this.restaurantId = restaurantId
        this.category = category
        this.price = price
        this.rating = rating
    }
}