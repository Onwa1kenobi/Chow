package io.julius.chow.domain.interactor.splash

import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.splash.UserPresentInteractor.Params
import io.julius.chow.domain.interactor.splash.UserPresentInteractor.Params.ParamType.CHECK_USER_PRESENCE
import io.julius.chow.domain.interactor.splash.UserPresentInteractor.Params.ParamType.CHECK_USER_TYPE
import io.julius.chow.domain.repository.ChowRepository
import io.julius.chow.domain.repository.RestaurantRepository
import javax.inject.Inject

class UserPresentInteractor @Inject constructor(
    private val chowRepository: ChowRepository,
    private val restaurantRepository: RestaurantRepository
) :
    Interactor<Params, Result<Any>>() {

    override suspend fun run(params: Params): Result<Any> {
        return when (params.paramType) {
            CHECK_USER_PRESENCE -> chowRepository.isUserLoggedIn()
            CHECK_USER_TYPE -> {
                val customer = chowRepository.fetchCurrentUser()
                val restaurant = restaurantRepository.fetchCurrentRestaurant()
                if (customer.profileComplete) Result.Success(customer) else Result.Success(restaurant)
            }
        }
    }

    class Params(val paramType: ParamType = CHECK_USER_PRESENCE, val user: Any? = null) {
        /**
         * Defines the types of params this interactor can take
         */
        enum class ParamType {
            CHECK_USER_PRESENCE,
            CHECK_USER_TYPE
        }
    }
}