package io.julius.chow.main.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.food.DeleteOrderInteractor
import io.julius.chow.domain.interactor.food.GetOrdersInteractor
import io.julius.chow.domain.interactor.profile.GetUserInteractor
import io.julius.chow.domain.model.UserModel
import io.julius.chow.mapper.OrderMapper
import io.julius.chow.mapper.UserMapper
import io.julius.chow.model.Order
import io.julius.chow.model.User
import io.julius.chow.util.Event
import javax.inject.Inject

class OrderViewModel @Inject constructor(
    private val getOrdersInteractor: GetOrdersInteractor,
    private val deleteOrderInteractor: DeleteOrderInteractor,
    private val getUserInteractor: GetUserInteractor
) : ViewModel() {

    // LiveData object for view state interaction
    val orderViewContract: MutableLiveData<Event<OrderViewContract>> = MutableLiveData()

    // public LiveData variable to expose returned list of orders
    val orders = MutableLiveData<List<Order>>()

    val currentUser = MutableLiveData<User>()

    // variable for the total cost of orders
    var totalOrderCost: Double = 0.0

    // Tax calculated as 2%
    val tax: Double get() = totalOrderCost * (2 / 100)

    // Delivery cost simply calculated as 4% of total cost plus 200.
    // More complex method could use address distance and stuff.
    val deliveryCharge: Double get() = (totalOrderCost * (3 / 100)) + 200

    fun getOrders() {
        // Display progress bar
        orderViewContract.postValue(Event(OrderViewContract.ProgressDisplay(true)))

        getOrdersInteractor.execute(Interactor.None()) {
            it.subscribe({ result ->
                // Hide progress bar
                orderViewContract.postValue(Event(OrderViewContract.ProgressDisplay(false)))

                when (result) {
                    is Result.Success -> {
                        // Map all order model objects and post the list
                        val response = result.data.map { orderModel ->
                            OrderMapper.mapFromModel(orderModel)
                        }

                        orders.postValue(response)
                    }

                    is Result.Failure -> {
                        // Display error message to user
                        orderViewContract.postValue(
                            Event(OrderViewContract.MessageDisplay(result.exception.toString()))
                        )
                    }
                }
            }, { throwable ->
                // Hide progress bar
                orderViewContract.postValue(Event(OrderViewContract.ProgressDisplay(false)))

                // Display error message to user
                orderViewContract.postValue(
                    Event(OrderViewContract.MessageDisplay(throwable.localizedMessage.toString()))
                )
            })
        }
    }

    fun removeOrder(order: Order) {
        deleteOrderInteractor.execute(OrderMapper.mapToModel(order))
    }

    fun getCurrentUser() {
        getUserInteractor.execute(false) {
            currentUser.postValue(UserMapper.mapFromModel(it as UserModel))
        }
    }

    fun placeOrder(address: String, deliveryTime: String) {

    }
}