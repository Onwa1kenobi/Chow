package io.julius.chow.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Restaurants")
class RestaurantEntity() {

    @PrimaryKey
    lateinit var id: String
    lateinit var name: String
    lateinit var imageUrl: String
    lateinit var phoneNumber: String
    lateinit var description: String
    lateinit var address: String
    lateinit var location: String
    var locationLongitude: Double = 0.0
    var locationLatitude: Double = 0.0
    // Boolean variable to check if the profile was successfully created.
    var profileComplete: Boolean = false
    var isCurrentRestaurant = false


    @Ignore
    constructor(
        id: String,
        name: String,
        imageUrl: String,
        phoneNumber: String,
        description: String,
        address: String,
        location: String,
        locationLongitude: Double,
        locationLatitude: Double,
        profileComplete: Boolean = false
    ) : this() {
        this.id = id
        this.name = name
        this.imageUrl = imageUrl
        this.phoneNumber = phoneNumber
        this.description = description
        this.address = address
        this.location = location
        this.locationLongitude = locationLongitude
        this.locationLatitude = locationLatitude
        this.profileComplete = profileComplete
    }
}