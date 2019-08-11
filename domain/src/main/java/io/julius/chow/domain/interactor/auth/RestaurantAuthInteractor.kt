package io.julius.chow.domain.interactor.auth

import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.auth.RestaurantAuthInteractor.Params
import io.julius.chow.domain.interactor.auth.RestaurantAuthInteractor.Params.ParamType.AUTHENTICATE_RESTAURANT
import io.julius.chow.domain.interactor.auth.RestaurantAuthInteractor.Params.ParamType.UPDATE_RESTAURANT_INFO
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.domain.repository.RestaurantRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * This interactor takes in a Param object to operate on two occasions, authenticate the restaurant user or save the user details
 * The return type is set to Any; so that we can return the UserModel type or a boolean if the user object was successfully saved.
 * Since we are using FireStore which is asynchronous, we don't want to return anything.
 * Rather, we pass in functions as parameters, so they can be called with whatever data we may be expecting,
 * and handle the return in our ViewModels. Think Higher Order Functions.
 */
class RestaurantAuthInteractor @Inject constructor(private val restaurantRepository: RestaurantRepository) :
    Interactor<Params, Result<Any>>(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

//    private lateinit var job: Job

    override suspend fun run(params: Params): Result<Any> {
        when (params.paramType) {
            AUTHENTICATE_RESTAURANT -> {
                // Authenticate current active user
                when (val result = restaurantRepository.authenticateRestaurant()) {
                    is Result.Success -> {
                        // Get underlying data
                        return if (result.data.profileComplete) {
                            // User profile was completely created, save locally and return true
                            restaurantRepository.saveRestaurantLocally(result.data)
                            Result.Success(true)
                        } else {
                            // User profile was not completely created, return restaurant object for detail screen
                            Result.Success(result.data)
                        }
                    }

                    is Result.Failure -> {
                        return Result.Failure(result.exception)
                    }
                }
            }

            UPDATE_RESTAURANT_INFO -> {
                return when (val result = restaurantRepository.saveRestaurant(params.restaurantModel!!)) {
                    is Result.Success -> {
                        // The restaurant user data was successfully saved, save locally and return true
                        Result.Success(true)
                    }

                    is Result.Failure -> {
                        // A failure occurred. Return an error message
                        Result.Failure(result.exception)
                    }
                }
            }
        }
    }

    class Params(
        val paramType: ParamType = AUTHENTICATE_RESTAURANT,
        val restaurantModel: RestaurantModel? = null
    ) {
        /**
         * Defines the types of params this interactor can take
         */
        enum class ParamType {
            AUTHENTICATE_RESTAURANT,
            UPDATE_RESTAURANT_INFO
        }
    }
}