package io.julius.chow.domain.interactor.food

import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.OrderModel
import io.julius.chow.domain.repository.ChowRepository
import javax.inject.Inject

class SaveOrderInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<OrderModel, String>() {

    override suspend fun run(params: OrderModel): String {
        // Check if this order has been added before
        val orderModel = chowRepository.getOrder(params.id)

        if (orderModel == null) {
            // Order has not been added before, simply add it as is
            val saved = chowRepository.saveOrder(params)
            return if (saved) {
                "Order was added successfully"
            } else {
                "Failed to add order, try again"
            }
        } else {
            // Order has been added before, lets merge the quantities of the existing and new orders and save it
            params.quantity = params.quantity + orderModel.quantity
            val saved = chowRepository.saveOrder(params)
            return if (saved) {
                "Order was updated successfully"
            } else {
                "Failed to update order, try again"
            }
        }
    }
}