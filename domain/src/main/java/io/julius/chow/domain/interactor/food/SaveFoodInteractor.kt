package io.julius.chow.domain.interactor.food

import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.FoodModel
import io.julius.chow.domain.repository.RestaurantRepository
import javax.inject.Inject

class SaveFoodInteractor @Inject constructor(private val restaurantRepository: RestaurantRepository) :
    Interactor<FoodModel, Result<Any>>() {

    override suspend fun run(params: FoodModel): Result<Any> {
        if (params.restaurantId.isBlank()) {
            params.restaurantId = restaurantRepository.fetchCurrentRestaurant().id
        }
        return restaurantRepository.saveFood(params)
    }
}