package io.julius.chow.domain.interactor.food

import io.julius.chow.domain.ChowRepository
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.FoodModel
import io.reactivex.Flowable
import javax.inject.Inject

class GetMenuInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<String, Flowable<Result<List<FoodModel>>>>() {

    override suspend fun run(params: String): Flowable<Result<List<FoodModel>>> {
        return chowRepository.getMenu(params)
    }
}