package io.julius.chow.domain.interactor.food

import io.julius.chow.domain.ChowRepository
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.OrderModel
import io.julius.chow.domain.model.RestaurantModel
import io.reactivex.Flowable
import javax.inject.Inject

class GetOrdersInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Interactor.None, Flowable<Result<List<OrderModel>>>>() {

    override suspend fun run(params: None): Flowable<Result<List<OrderModel>>> {
        return chowRepository.getOrders()
    }
}