package io.julius.chow.main.restaurants

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.food.SaveOrderInteractor
import io.julius.chow.domain.interactor.restaurant.RestaurantMenuInteractor
import io.julius.chow.mapper.FoodMapper
import io.julius.chow.mapper.OrderMapper
import io.julius.chow.model.Food
import io.julius.chow.model.Order
import io.julius.chow.util.Event
import javax.inject.Inject

class RestaurantDetailsViewModel @Inject constructor(
    private val menuInteractor: RestaurantMenuInteractor,
    private val saveOrderInteractor: SaveOrderInteractor
) :
    ViewModel() {

    // LiveData object for view state interaction
    val restaurantViewContract: MutableLiveData<Event<RestaurantViewContract>> = MutableLiveData()

    // public LiveData variable to expose returned restaurant menu list
    val menu = MutableLiveData<List<Food>>()

    fun fetchRestaurantMenu(restaurantId: String) {
        menuInteractor.execute(restaurantId) {
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

    fun addOrder(food: Food) {
        // Construct an order object with the food passed
        val order = Order(food)
        // Save order to the database
        saveOrderInteractor.execute(OrderMapper.mapToModel(order)) { message ->
            restaurantViewContract.postValue(Event(RestaurantViewContract.MessageDisplay(message)))
        }
    }
}