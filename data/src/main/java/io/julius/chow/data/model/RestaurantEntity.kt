package io.julius.chow.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint

@Entity(tableName = "Restaurants")
data class RestaurantEntity(val version: Int = 1) {

    @PrimaryKey
    lateinit var id: String
    lateinit var name: String
    lateinit var imageUrl: String
    lateinit var phoneNumber: String
    lateinit var description: String
    lateinit var address: String
    lateinit var emailAddress: String
    lateinit var location: String
    @get:Exclude
    var locationLongitude: Double = 0.0
    @get:Exclude
    var locationLatitude: Double = 0.0
    @Ignore
    var latlng: GeoPoint = GeoPoint(locationLatitude, locationLongitude)
        get() = GeoPoint(locationLatitude, locationLongitude)
    // Boolean variable to check if the profile was successfully created.
    var profileComplete: Boolean = false
    @get:Exclude
    var isCurrentRestaurant = false


    @Ignore
    constructor(
        id: String,
        name: String,
        imageUrl: String,
        phoneNumber: String,
        description: String,
        address: String,
        emailAddress: String,
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
        this.emailAddress = emailAddress
        this.location = location
        this.locationLongitude = locationLongitude
        this.locationLatitude = locationLatitude
        this.profileComplete = profileComplete
    }
}