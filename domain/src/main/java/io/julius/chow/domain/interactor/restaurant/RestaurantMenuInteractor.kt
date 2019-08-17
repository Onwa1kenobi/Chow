package io.julius.chow.domain.interactor.restaurant

import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.FoodModel
import io.julius.chow.domain.repository.RestaurantRepository
import io.reactivex.Flowable
import javax.inject.Inject

class RestaurantMenuInteractor @Inject constructor(private val restaurantRepository: RestaurantRepository) :
    Interactor<String, Flowable<Result<List<FoodModel>>>>() {

    override suspend fun run(params: String): Flowable<Result<List<FoodModel>>> {
        return restaurantRepository.fetchRestaurantMenu(params)
    }
}