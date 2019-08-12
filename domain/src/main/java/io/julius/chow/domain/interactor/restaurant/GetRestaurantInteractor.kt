package io.julius.chow.domain.interactor.restaurant

import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.repository.RestaurantRepository
import javax.inject.Inject

/**
 * Interactor responsible for getting a restaurant model
 */
class GetRestaurantInteractor @Inject constructor(private val restaurantRepository: RestaurantRepository) :
    Interactor<Boolean, Any>() {

    // params here refers to a boolean variable passed to tell if a reactive restaurant user object should be fetched or simply
    // a one time fetch operation.
    override suspend fun run(params: Boolean): Any {
        return if (params) {
            restaurantRepository.getCurrentRestaurant()
        } else {
            restaurantRepository.fetchCurrentRestaurant()
        }
    }
}