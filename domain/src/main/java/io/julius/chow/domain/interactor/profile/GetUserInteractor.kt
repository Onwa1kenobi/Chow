package io.julius.chow.domain.interactor.profile

import io.julius.chow.domain.ChowRepository
import io.julius.chow.domain.interactor.Interactor
import javax.inject.Inject

/**
 * Interactor responsible for getting a user model
 */
class GetUserInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Boolean, Any>() {

    // params here refers to a boolean variable passed to tell if a reactive user object should be fetched or simply
    // a one time fetch operation.
    override suspend fun run(params: Boolean): Any {
        return if (params) {
            chowRepository.getCurrentUser()
        } else {
            chowRepository.fetchCurrentUser()
        }
    }
}