package io.julius.chow.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.auth.AuthInteractor
import io.julius.chow.domain.interactor.profile.GetUserInteractor
import io.julius.chow.domain.model.UserModel
import io.julius.chow.mapper.UserMapper
import io.julius.chow.model.User
import io.julius.chow.util.Event
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val getUserInteractor: GetUserInteractor
) : ViewModel() {

    // LiveData object for view state interaction
    val authContractData = MutableLiveData<Event<AuthViewContract>>()

    // private variable to hold current user info for the two fragments
    val currentUser = MutableLiveData<User>()

    fun authCurrentUser() {
        // Display progress bar
        authContractData.value = Event(AuthViewContract.ProgressDisplay(true))

        // Set AuthInteractor param to update user info and pass the function that will be notified when the operation is done
        val authInteractorParams = AuthInteractor.Params()
        authInteractor.execute(authInteractorParams) {
            // We are passing a function as a parameter which will be called when our respective operation occurs
            // Hide progress bar
            authContractData.value = Event(AuthViewContract.ProgressDisplay(false))
            when (it) {
                is Result.Success -> {
                    if (it.data is Boolean) {
                        // User profile was completely created, navigate to the MainActivity
                        authContractData.value = Event(AuthViewContract.NavigateToHome)
                    } else {
                        // User profile was not completely created, navigate to the SignUpFragment
                        authContractData.value = Event(AuthViewContract.NavigateToSignUp)
                        currentUser.postValue(UserMapper.mapFromModel(it.data as UserModel))
                    }
                }

                is Result.Failure -> {
                    // Display error message to user
                    authContractData.value = Event(AuthViewContract.MessageDisplay(it.exception.toString()))
                }
            }
        }
    }

    fun completeUserRegistration(userName: String, userAddress: String) {
        // Display progress bar
        authContractData.value = Event(AuthViewContract.ProgressDisplay(true))

        // Update the details of the current user
        currentUser.value?.apply {
            name = userName
            address = userAddress
            profileComplete = true
        }

        // Set AuthInteractor param to update user info and pass the function that will be notified when the operation is done
        val authInteractorParams =
            AuthInteractor.Params(
                AuthInteractor.Params.ParamType.UPDATE_USER_INFO,
                UserMapper.mapToModel(currentUser.value!!)
            )
        authInteractor.execute(authInteractorParams) {
            // Hide progress bar
            authContractData.value = Event(AuthViewContract.ProgressDisplay(false))
            when (it) {
                is Result.Success -> {
                    // The user data was successfully saved, now we can navigate to the MainActivity
                    authContractData.value = Event(AuthViewContract.NavigateToHome)
                }

                is Result.Failure -> {
                    // Display error message to user
                    authContractData.value = Event(AuthViewContract.MessageDisplay(it.exception.toString()))
                }
            }
        }
    }

    fun getCurrentUser() {
        // Display progress bar
        authContractData.value = Event(AuthViewContract.ProgressDisplay(true))

        getUserInteractor.execute(false) {
            // Hide progress bar
            authContractData.value = Event(AuthViewContract.ProgressDisplay(false))
            currentUser.postValue(UserMapper.mapFromModel(it as UserModel))
        }
    }
}