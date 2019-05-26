package io.julius.chow.domain.model

class FoodModel(
    val id: String,
    val title: String,
    val imageUrl: String,
    val description: String,
    val restaurantId: String,
    val price: Double,
    val rating: Double
)