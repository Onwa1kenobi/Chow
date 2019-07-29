package io.julius.chow.domain.interactor.food

import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.OrderModel
import io.julius.chow.domain.repository.ChowRepository
import javax.inject.Inject

class DeleteOrderInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<OrderModel, Unit>() {

    override suspend fun run(params: OrderModel) {
        // Delete the order from the database
        chowRepository.deleteOrder(params)
    }
}