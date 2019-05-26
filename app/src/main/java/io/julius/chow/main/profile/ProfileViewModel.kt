package io.julius.chow.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.profile.GetUserInteractor
import io.julius.chow.mapper.UserMapper
import io.julius.chow.model.User
import io.julius.chow.util.Event
import javax.inject.Inject

class ProfileViewModel @Inject constructor(getUserInteractor: GetUserInteractor) : ViewModel() {

    // LiveData object for view state interaction
    val profileViewContract: MutableLiveData<Event<ProfileViewContract>> = MutableLiveData()

    // public LiveData variable to expose the user
    val user = MutableLiveData<User>()

    init {
        // Display progress bar
        profileViewContract.postValue(Event(ProfileViewContract.ProgressDisplay(true)))

        getUserInteractor.execute(Interactor.None()) {
            it.subscribe({ result ->
                // Hide progress bar
                profileViewContract.postValue(Event(ProfileViewContract.ProgressDisplay(false)))

                when (result) {
                    is Result.Success -> {
                        // Map to layer specific model and pass to view.
                        user.postValue(UserMapper.mapFromModel(result.data))
                    }

                    is Result.Failure -> {
                        // Display error message to user
                        profileViewContract.postValue(
                            Event(ProfileViewContract.MessageDisplay(result.exception.toString()))
                        )
                    }
                }
            }, { throwable ->
                // Hide progress bar
                profileViewContract.postValue(Event(ProfileViewContract.ProgressDisplay(false)))

                // Display error message to user
                profileViewContract.postValue(
                    Event(ProfileViewContract.MessageDisplay(throwable.localizedMessage.toString()))
                )
            })
        }
    }
}