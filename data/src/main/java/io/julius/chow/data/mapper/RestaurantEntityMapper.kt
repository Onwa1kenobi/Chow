package io.julius.chow.data.mapper

import io.julius.chow.data.model.RestaurantEntity
import io.julius.chow.domain.model.RestaurantModel

/**
 * Map a [RestaurantEntity] to and from a [RestaurantModel] instance when data is moving between
 * this layer and the Domain layer
 */
object RestaurantEntityMapper : Mapper<RestaurantEntity, RestaurantModel> {

    /**
     * Map an instance of a [RestaurantModel] to a [RestaurantEntity] model
     */
    override fun mapToEntity(type: RestaurantModel): RestaurantEntity {
        return RestaurantEntity(
            id = type.id,
            name = type.name,
            imageUrl = type.imageUrl,
            phoneNumber = type.phoneNumber,
            description = type.description,
            address = type.address,
            location = type.location,
            locationLongitude = type.locationLongitude,
            locationLatitude = type.locationLatitude,
            profileComplete = type.profileComplete
        )
    }

    /**
     * Map a [RestaurantEntity] instance to a [RestaurantModel] instance
     */
    override fun mapFromEntity(type: RestaurantEntity): RestaurantModel {
        return RestaurantModel(
            id = type.id,
            name = type.name,
            imageUrl = type.imageUrl,
            phoneNumber = type.phoneNumber,
            description = type.description,
            address = type.address,
            location = type.location,
            locationLongitude = type.locationLongitude,
            locationLatitude = type.locationLatitude,
            profileComplete = type.profileComplete
        )
    }
}