package io.julius.chow.main.restaurants

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.auth.SignOutInteractor
import io.julius.chow.domain.interactor.restaurant.GetRestaurantInteractor
import io.julius.chow.domain.model.RestaurantModel
import io.julius.chow.main.profile.ProfileViewContract
import io.julius.chow.mapper.RestaurantMapper
import io.julius.chow.model.Restaurant
import io.julius.chow.util.Event
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RestaurantProfileViewModel @Inject constructor(
    getRestaurantInteractor: GetRestaurantInteractor,
    private val signOutInteractor: SignOutInteractor
) :
    ViewModel() {

    // LiveData object for view state interaction
    val profileViewContract: MutableLiveData<Event<ProfileViewContract>> = MutableLiveData()

    // public LiveData variable to expose the restaurant
    val restaurant = MutableLiveData<Restaurant>()

    private val disposable = CompositeDisposable()

    init {
        // Display progress bar
        profileViewContract.postValue(Event(ProfileViewContract.ProgressDisplay(true)))

        getRestaurantInteractor.execute(true) {
            disposable.add((it as Flowable<*>).subscribe({ result ->
                // Hide progress bar
                profileViewContract.postValue(Event(ProfileViewContract.ProgressDisplay(false)))

                when (result) {
                    is Result.Success<*> -> {
                        // Map to layer specific model and pass to view.
                        restaurant.postValue(RestaurantMapper.mapFromModel(result.data as RestaurantModel))
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
            }))
        }
    }

    fun signOut() {
        signOutInteractor.execute(Interactor.None()) {
            profileViewContract.postValue(
                Event(ProfileViewContract.NavigateToAuth)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}