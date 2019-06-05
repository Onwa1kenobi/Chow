package io.julius.chow.data.mapper

import io.julius.chow.data.model.PlacedOrderEntity
import io.julius.chow.domain.model.PlacedOrderModel

/**
 * Map a [PlacedOrderEntity] to and from a [PlacedOrderModel] instance when data is moving between
 * this layer and the Domain layer
 */
object PlacedOrderEntityMapper : Mapper<PlacedOrderEntity, PlacedOrderModel> {

    /**
     * Map an instance of a [PlacedOrderModel] to a [PlacedOrderEntity] model
     */
    override fun mapToEntity(type: PlacedOrderModel): PlacedOrderEntity {
        return PlacedOrderEntity(
            id = type.id,
            orders = type.orders.map { OrderEntityMapper.mapToEntity(it) },
            user = UserEntityMapper.mapToEntity(type.user),
            createdAt = type.createdAt,
            deliveryTime = type.deliveryTime,
            subTotalCost = type.subTotalCost,
            tax = type.tax,
            deliveryCharge = type.deliveryCharge
        )
    }

    /**
     * Map a [PlacedOrderEntity] instance to a [PlacedOrderModel] instance
     */
    override fun mapFromEntity(type: PlacedOrderEntity): PlacedOrderModel {
        return PlacedOrderModel(
            id = type.id,
            orders = type.orders.map { OrderEntityMapper.mapFromEntity(it) },
            user = UserEntityMapper.mapFromEntity(type.user),
            createdAt = type.createdAt,
            deliveryTime = type.deliveryTime,
            subTotalCost = type.subTotalCost,
            tax = type.tax,
            deliveryCharge = type.deliveryCharge
        )
    }
}