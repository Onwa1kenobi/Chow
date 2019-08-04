package io.julius.chow.domain.model

class RestaurantModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val phoneNumber: String,
    val description: String,
    val address: String,
    val emailAddress: String,
    val location: String,
    val locationLongitude: Double,
    val locationLatitude: Double,
    val profileComplete: Boolean
)