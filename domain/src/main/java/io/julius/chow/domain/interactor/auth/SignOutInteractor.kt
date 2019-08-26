package io.julius.chow.domain.interactor.auth

import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.repository.ChowRepository
import javax.inject.Inject

class SignOutInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Interactor.None, Result<Boolean>>() {

    override suspend fun run(params: None): Result<Boolean> {
        return chowRepository.signOut()
    }
}