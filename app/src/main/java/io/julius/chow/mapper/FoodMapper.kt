package io.julius.chow.mapper

import io.julius.chow.domain.model.FoodModel
import io.julius.chow.model.Food

/**
 * Map a [Food] to and from a [FoodModel] instance when data is moving between
 * this layer and the Domain layer
 */
object FoodMapper : Mapper<Food, FoodModel> {

    /**
     * Map an instance of a [FoodModel] to a [Food] model
     */
    override fun mapFromModel(type: FoodModel): Food {
        return Food(
            id = type.id,
            title = type.title,
            imageUrl = type.imageUrl,
            description = type.description,
            restaurantId = type.restaurantId,
            price = type.price,
            rating = type.rating
        )
    }

    /**
     * Map a [Food] instance to a [FoodModel] instance
     */
    override fun mapToModel(type: Food): FoodModel {
        return FoodModel(
            id = type.id,
            title = type.title,
            imageUrl = type.imageUrl,
            description = type.description,
            restaurantId = type.restaurantId,
            price = type.price,
            rating = type.rating
        )
    }
}