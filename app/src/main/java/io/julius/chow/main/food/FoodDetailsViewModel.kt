package io.julius.chow.main.food

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.interactor.food.SaveOrderInteractor
import io.julius.chow.mapper.OrderMapper
import io.julius.chow.model.Food
import io.julius.chow.model.Order
import io.julius.chow.util.Event
import javax.inject.Inject

class FoodDetailsViewModel @Inject constructor(private val saveOrderInteractor: SaveOrderInteractor) : ViewModel() {

    lateinit var order: Order

    // LiveData object for view state interaction
    val foodViewContract: MutableLiveData<Event<FoodViewContract>> = MutableLiveData()

    // public LiveData variable to expose active food order quantity
    val orderQuantity = MutableLiveData<Int>()

    fun createOrder(food: Food) {
        orderQuantity.value = 1
        order = Order(food = food, quantity = orderQuantity.value!!)
    }

    fun decrementOrder() {
        // Set minimum order limit to 1
        if (orderQuantity.value!! > 1) {
            orderQuantity.postValue(orderQuantity.value!! - 1)
        }
    }

    fun incrementOrder() {
        // Set maximum order limit to 50
        if (order.quantity < 50) {
            orderQuantity.postValue(orderQuantity.value!! + 1)
        }
    }

    fun addOrder() {
        order.quantity = orderQuantity.value!!

        saveOrderInteractor.execute(OrderMapper.mapToModel(order)) { message ->
            foodViewContract.postValue(Event(FoodViewContract.MessageDisplay(message)))
        }
    }
}