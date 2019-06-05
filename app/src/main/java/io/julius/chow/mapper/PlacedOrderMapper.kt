package io.julius.chow.mapper

import io.julius.chow.domain.model.PlacedOrderModel
import io.julius.chow.model.PlacedOrder

/**
 * Map a [PlacedOrder] to and from a [PlacedOrderModel] instance when data is moving between
 * this layer and the Domain layer
 */
object PlacedOrderMapper : Mapper<PlacedOrder, PlacedOrderModel> {

    /**
     * Map an instance of a [PlacedOrder] to a [PlacedOrderModel] model
     */
    override fun mapToModel(type: PlacedOrder): PlacedOrderModel {
        return PlacedOrderModel(
            id = type.id,
            orders = type.orders.map { OrderMapper.mapToModel(it) },
            user = UserMapper.mapToModel(type.user),
            createdAt = type.createdAt,
            deliveryTime = type.deliveryTime,
            subTotalCost = type.subTotalCost,
            tax = type.tax,
            deliveryCharge = type.deliveryCharge
        )
    }

    /**
     * Map a [PlacedOrderModel] instance to a [PlacedOrder] instance
     */
    override fun mapFromModel(type: PlacedOrderModel): PlacedOrder {
        return PlacedOrder(
            id = type.id,
            orders = type.orders.map { OrderMapper.mapFromModel(it) },
            user = UserMapper.mapFromModel(type.user),
            createdAt = type.createdAt,
            deliveryTime = type.deliveryTime,
            subTotalCost = type.subTotalCost,
            tax = type.tax,
            deliveryCharge = type.deliveryCharge
        )
    }
}