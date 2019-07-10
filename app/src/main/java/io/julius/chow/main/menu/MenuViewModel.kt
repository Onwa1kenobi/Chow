package io.julius.chow.main.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.food.GetMenuInteractor
import io.julius.chow.main.restaurants.RestaurantViewContract
import io.julius.chow.mapper.FoodMapper
import io.julius.chow.model.Food
import io.julius.chow.util.Event
import javax.inject.Inject

class MenuViewModel @Inject constructor(private val menuInteractor: GetMenuInteractor) : ViewModel() {

    // LiveData object for view state interaction
    val restaurantViewContract: MutableLiveData<Event<RestaurantViewContract>> = MutableLiveData()

    // public LiveData variable to expose returned restaurant menu list
    val menu = MutableLiveData<List<Food>>()

    fun fetchCategoryMenu(category: String) {
        menuInteractor.execute(category) {
            it.subscribe({ result ->
                when (result) {
                    is Result.Success -> {
                        // Map all restaurant model objects and post the list
                        val response = result.data.map { foodModel ->
                            FoodMapper.mapFromModel(foodModel)
                        }
                        menu.postValue(response)
                    }

                    is Result.Failure -> {
                        // Display error message to user
                        restaurantViewContract.postValue(
                            Event(RestaurantViewContract.MessageDisplay(result.exception.toString()))
                        )
                    }
                }
            }, { throwable ->
                // Display error message to user
                restaurantViewContract.postValue(
                    Event(RestaurantViewContract.MessageDisplay(throwable.localizedMessage.toString()))
                )
            })
        }
    }
}