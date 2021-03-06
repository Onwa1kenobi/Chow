package io.julius.chow.main.restaurants

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.restaurant.RestaurantInteractor
import io.julius.chow.mapper.RestaurantMapper
import io.julius.chow.model.Restaurant
import io.julius.chow.util.Event
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RestaurantViewModel @Inject constructor(private val restaurantInteractor: RestaurantInteractor) : ViewModel() {

    // LiveData object for view state interaction
    val restaurantViewContract: MutableLiveData<Event<RestaurantViewContract>> = MutableLiveData()

    // public LiveData variable to expose returned list of restaurants
    val restaurants = MutableLiveData<List<Restaurant>>()

    private val disposable: CompositeDisposable = CompositeDisposable()

    fun fetchRestaurants() {
        // Display progress bar
        restaurantViewContract.postValue(Event(RestaurantViewContract.ProgressDisplay(true)))

        restaurantInteractor.execute(Interactor.None()) {
            disposable.add(it.subscribe({ result ->
                // Hide progress bar
                restaurantViewContract.postValue(Event(RestaurantViewContract.ProgressDisplay(false)))

                when (result) {
                    is Result.Success -> {
                        // Map all restaurant model objects and post the list
                        val response = result.data.map { restaurantModel ->
                            RestaurantMapper.mapFromModel(restaurantModel)
                        }

                        restaurants.postValue(response)
                    }

                    is Result.Failure -> {
                        // Display error message to user
                        restaurantViewContract.postValue(
                            Event(RestaurantViewContract.MessageDisplay(result.exception.toString()))
                        )
                    }
                }
            }, { throwable ->
                // Hide progress bar
                restaurantViewContract.postValue(Event(RestaurantViewContract.ProgressDisplay(false)))

                // Display error message to user
                restaurantViewContract.postValue(
                    Event(RestaurantViewContract.MessageDisplay(throwable.localizedMessage.toString()))
                )
            }))
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}