package io.julius.chow.data.mapper

import io.julius.chow.data.model.OrderEntity
import io.julius.chow.domain.model.OrderModel
import io.julius.chow.domain.model.OrderState

/**
 * Map a [OrderEntity] to and from a [OrderModel] instance when data is moving between
 * this layer and the Domain layer
 */
object OrderEntityMapper : Mapper<OrderEntity, OrderModel> {

    /**
     * Map an instance of a [OrderModel] to a [OrderEntity] model
     */
    override fun mapToEntity(type: OrderModel): OrderEntity {
        return OrderEntity(
            id = type.id,
            title = type.title,
            imageUrl = type.imageUrl,
            description = type.description,
            restaurantId = type.restaurantId,
            price = type.price,
            rating = type.rating,
            quantity = type.quantity,
            status = type.status.value,
            inHistory = type.inHistory
        )
    }

    /**
     * Map a [OrderEntity] instance to a [OrderModel] instance
     */
    override fun mapFromEntity(type: OrderEntity): OrderModel {
        return OrderModel(
            id = type.id,
            title = type.title,
            imageUrl = type.imageUrl,
            description = type.description,
            restaurantId = type.restaurantId,
            price = type.price,
            rating = type.rating,
            quantity = type.quantity,
            status = OrderState.getOrderState(type.status),
            inHistory = type.inHistory
        )
    }
}