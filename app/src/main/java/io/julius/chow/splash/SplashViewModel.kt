package io.julius.chow.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.splash.UserPresentInteractor
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val userPresentInteractor: UserPresentInteractor) : ViewModel() {

    var userIsLoggedIn = MutableLiveData<Boolean>()

    init {
        isUserLoggedIn()
    }

    private fun isUserLoggedIn() = userPresentInteractor.execute(Interactor.None()) {
        when (it) {
            is Result.Success<*> -> userIsLoggedIn.postValue(it.data as Boolean)
            else -> userIsLoggedIn.postValue(false)
        }
    }
}