package io.julius.chow.mapper

import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.model.Restaurant

/**
 * Map a [Restaurant] to and from a [RestaurantModel] instance when data is moving between
 * this layer and the Domain layer
 */
object RestaurantMapper : Mapper<Restaurant, RestaurantModel> {

    /**
     * Map an instance of a [RestaurantModel] to a [Restaurant] model
     */
    override fun mapFromModel(type: RestaurantModel): Restaurant {
        return Restaurant(
            id = type.id,
            name = type.name,
            imageUrl = type.imageUrl,
            phoneNumber = type.phoneNumber,
            description = type.description,
            address = type.address,
            emailAddress = type.emailAddress,
            location = type.location,
            locationLongitude = type.locationLongitude,
            locationLatitude = type.locationLatitude,
            profileComplete = type.profileComplete
        )
    }

    /**
     * Map a [Restaurant] instance to a [RestaurantModel] instance
     */
    override fun mapToModel(type: Restaurant): RestaurantModel {
        return RestaurantModel(
            id = type.id,
            name = type.name,
            imageUrl = type.imageUrl,
            phoneNumber = type.phoneNumber,
            description = type.description,
            address = type.address,
            emailAddress = type.emailAddress,
            location = type.location,
            locationLongitude = type.locationLongitude,
            locationLatitude = type.locationLatitude,
            profileComplete = type.profileComplete
        )
    }
}