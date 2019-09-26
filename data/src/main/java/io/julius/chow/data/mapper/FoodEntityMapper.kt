package io.julius.chow.data.mapper

import io.julius.chow.data.model.FoodEntity
import io.julius.chow.domain.model.FoodModel

/**
 * Map a [FoodEntity] to and from a [FoodModel] instance when data is moving between
 * this layer and the Domain layer
 */
object FoodEntityMapper : Mapper<FoodEntity, FoodModel> {

    /**
     * Map an instance of a [FoodModel] to a [FoodEntity] model
     */
    override fun mapToEntity(type: FoodModel): FoodEntity {
        return FoodEntity(
            id = type.id,
            title = type.title,
            imageUrl = type.imageUrl,
            description = type.description,
            category = type.category,
            restaurantId = type.restaurantId,
            price = type.price,
            rating = type.rating
        )
    }

    /**
     * Map a [FoodEntity] instance to a [FoodModel] instance
     */
    override fun mapFromEntity(type: FoodEntity): FoodModel {
        return FoodModel(
            id = type.id,
            title = type.title,
            imageUrl = type.imageUrl,
            description = type.description,
            category = type.category,
            restaurantId = type.restaurantId,
            price = type.price,
            rating = type.rating
        )
    }
}