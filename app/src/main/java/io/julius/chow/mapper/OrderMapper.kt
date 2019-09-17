package io.julius.chow.mapper

import io.julius.chow.domain.model.OrderModel
import io.julius.chow.model.Food
import io.julius.chow.model.Order

/**
 * Map a [Order] to and from a [OrderModel] instance when data is moving between
 * this layer and the Domain layer
 */
object OrderMapper : Mapper<Order, OrderModel> {

    /**
     * Map an instance of a [OrderModel] to a [Order] model
     */
    override fun mapFromModel(type: OrderModel): Order {
        val orderFood = Food(
            id = type.id,
            title = type.title,
            imageUrl = type.imageUrl,
            description = type.description,
            restaurantId = type.restaurantId,
            price = type.price,
            rating = type.rating
        )

        return Order(
            food = orderFood,
            quantity = type.quantity,
            status = type.status,
            inHistory = type.inHistory
        )
    }

    /**
     * Map a [Order] instance to a [OrderModel] instance
     */
    override fun mapToModel(type: Order): OrderModel {
        return OrderModel(
            id = type.food.id,
            title = type.food.title,
            imageUrl = type.food.imageUrl,
            description = type.food.description,
            restaurantId = type.food.restaurantId,
            price = type.food.price,
            rating = type.food.rating,
            quantity = type.quantity,
            status = type.status,
            inHistory = type.inHistory
        )
    }
}