package io.julius.chow.domain.model

import java.util.*

class PlacedOrderModel(
    val id: String,
    val orders: List<OrderModel>,
    val user: UserModel,
    val createdAt: Date,
    val deliveryTime: String,
    val subTotalCost: Double,
    val tax: Double,
    val deliveryCharge: Double
)