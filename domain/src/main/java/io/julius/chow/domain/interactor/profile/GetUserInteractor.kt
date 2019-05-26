package io.julius.chow.domain.interactor.profile

import io.julius.chow.domain.ChowRepository
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.model.UserModel
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Interactor responsible for getting a user model
 */
class GetUserInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Interactor.None, Flowable<Result<UserModel>>>() {

    override suspend fun run(params: None): Flowable<Result<UserModel>> {
        return chowRepository.getCurrentUser()
    }
}