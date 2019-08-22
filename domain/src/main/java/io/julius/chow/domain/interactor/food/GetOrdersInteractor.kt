package io.julius.chow.domain.interactor.food

import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.OrderModel
import io.julius.chow.domain.model.UserType
import io.julius.chow.domain.repository.ChowRepository
import io.julius.chow.domain.repository.RestaurantRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetOrdersInteractor @Inject constructor(
    private val chowRepository: ChowRepository,
    private val restaurantRepository: RestaurantRepository
) :
    Interactor<UserType, Flowable<Result<List<OrderModel>>>>() {

    override suspend fun run(params: UserType): Flowable<Result<List<OrderModel>>> {
        return when (params) {
            UserType.CUSTOMER -> chowRepository.getOrders()
            UserType.RESTAURANT -> {
                val currentRestaurant = restaurantRepository.fetchCurrentRestaurant()
                restaurantRepository.getOrders(currentRestaurant.id)
            }
        }
    }
}