package io.julius.chow.domain.interactor.splash

import io.julius.chow.domain.Exception
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.splash.UserPresentInteractor.Params
import io.julius.chow.domain.interactor.splash.UserPresentInteractor.Params.ParamType.*
import io.julius.chow.domain.repository.ChowRepository
import javax.inject.Inject

class UserPresentInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Params, Result<Any>>() {

    override suspend fun run(params: Params): Result<Any> {
        return when (params.paramType) {
            CHECK_USER_PRESENCE -> chowRepository.isUserLoggedIn()
            CHECK_USER_TYPE -> {
                val account = chowRepository.getCurrentLoggedAccount()
                if (account != null) {
                    Result.Success(account)
                } else {
                    Result.Failure(Exception.LocalDataNotFoundException)
                }
            }
            GET_CURRENT_ACCOUNT_TYPE -> {
                chowRepository.getCurrentLoggedAccountType()
            }
        }
    }

    class Params(val paramType: ParamType = CHECK_USER_PRESENCE, val user: Any? = null) {
        /**
         * Defines the types of params this interactor can take
         */
        enum class ParamType {
            CHECK_USER_PRESENCE,
            CHECK_USER_TYPE,
            GET_CURRENT_ACCOUNT_TYPE
        }
    }
}