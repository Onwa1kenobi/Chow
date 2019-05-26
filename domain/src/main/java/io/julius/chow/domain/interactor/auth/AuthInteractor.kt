package io.julius.chow.domain.interactor.auth

import io.julius.chow.domain.ChowRepository
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.auth.AuthInteractor.Params
import io.julius.chow.domain.interactor.auth.AuthInteractor.Params.ParamType.AUTHENTICATE_USER
import io.julius.chow.domain.interactor.auth.AuthInteractor.Params.ParamType.UPDATE_USER_INFO
import io.julius.chow.domain.model.UserModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * This interactor takes in a Param object to operate on two occasions, authenticate the user or save the user details
 * The return type is set to Any; so that we can return the UserModel type or a boolean if the user object was successfully saved.
 * Since we are using FireStore which is asynchronous, we don't want to return anything.
 * Rather, we pass in functions as parameters, so they can be called with whatever data we may be expecting,
 * and handle the return in our ViewModels. Think Higher Order Functions.
 */
class AuthInteractor @Inject constructor(private val chowRepository: ChowRepository) :
    Interactor<Params, Result<Any>>(), CoroutineScope {

//    override val coroutineContext: CoroutineContext = Dispatchers.IO

//    private lateinit var job: Job

    override suspend fun run(params: Params): Result<Any> {
        when (params.paramType) {
            AUTHENTICATE_USER -> {
                // Authenticate current active user
                when (val result = chowRepository.authenticateUser()) {
                    is Result.Success -> {
                        // Get underlying data
                        return if (result.data.profileComplete) {
                            // User profile was completely created, save locally and return true
//                            coroutineScope {
//                                launch {
                                    chowRepository.saveUserLocally(result.data)
//                                }
//                            }
                            Result.Success(true)
                        } else {
                            // User profile was not completely created, return false
                            Result.Success(result.data)
                        }
                    }

                    is Result.Failure -> {
                        return Result.Failure(result.exception)
                    }
                }
            }

            UPDATE_USER_INFO -> {
                return when (val result = chowRepository.saveUser(params.userModel!!)) {
                    is Result.Success -> {
                        // The user data was successfully saved, save locally and return true
                        Result.Success(true)
                    }

                    is Result.Failure -> {
                        // A failure occurred. Return an error message
                        Result.Failure(result.exception)
                    }
                }
            }

//            else -> {
//                coroutineScope {
//
//                }
//            }
        }
    }

    class Params(val paramType: ParamType = AUTHENTICATE_USER, val userModel: UserModel? = null) {
        /**
         * Defines the types of params this interactor can take
         */
        enum class ParamType {
            AUTHENTICATE_USER,
            UPDATE_USER_INFO
        }
    }
}