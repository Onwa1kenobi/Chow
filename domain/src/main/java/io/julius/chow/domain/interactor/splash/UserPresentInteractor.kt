package io.julius.chow.domain.interactor.splash

import io.julius.chow.domain.ChowRepository
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import javax.inject.Inject

class UserPresentInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Interactor.None, Result<Boolean>>() {

    override suspend fun run(params: None): Result<Boolean> {
        return chowRepository.isUserLoggedIn()
    }
}