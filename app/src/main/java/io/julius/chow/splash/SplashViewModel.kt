package io.julius.chow.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.splash.UserPresentInteractor
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val userPresentInteractor: UserPresentInteractor) : ViewModel() {

    var userIsLoggedIn = MutableLiveData<Boolean>()

    init {
        isUserLoggedIn()
    }

    private fun isUserLoggedIn() = userPresentInteractor.execute(UserPresentInteractor.Params()) {
        when (it) {
            is Result.Success<*> -> userIsLoggedIn.postValue(it.data as Boolean)
            else -> userIsLoggedIn.postValue(false)
        }
    }

    fun getCurrentLoggedUser(): Any {
        return userPresentInteractor.execute(UserPresentInteractor.Params(UserPresentInteractor.Params.ParamType.CHECK_USER_TYPE))
    }
}