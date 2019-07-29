package io.julius.chow.domain.interactor.restaurant

import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.domain.repository.ChowRepository
import io.reactivex.Flowable
import javax.inject.Inject

class RestaurantInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Interactor.None, Flowable<Result<List<RestaurantModel>>>>() {

    override suspend fun run(params: None): Flowable<Result<List<RestaurantModel>>> {
        return chowRepository.fetchRestaurants()
    }
}