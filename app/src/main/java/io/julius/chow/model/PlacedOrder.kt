package io.julius.chow.model

import java.util.*

class PlacedOrder(
    val id: String,
    val orders: List<Order>,
    val user: User,
    val createdAt: Date,
    val deliveryTime: String,
    val subTotalCost: Double,
    val tax: Double,
    val deliveryCharge: Double
)