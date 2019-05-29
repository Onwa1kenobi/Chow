package io.julius.chow.main.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.Interactor
import io.julius.chow.domain.interactor.food.DeleteOrderInteractor
import io.julius.chow.domain.interactor.food.GetOrdersInteractor
import io.julius.chow.mapper.OrderMapper
import io.julius.chow.model.Order
import io.julius.chow.util.Event
import javax.inject.Inject

class OrderViewModel @Inject constructor(
    private val getOrdersInteractor: GetOrdersInteractor,
    private val deleteOrderInteractor: DeleteOrderInteractor
) : ViewModel() {

    // LiveData object for view state interaction
    val orderViewContract: MutableLiveData<Event<OrderViewContract>> = MutableLiveData()

    // public LiveData variable to expose returned list of orders
    val orders = MutableLiveData<List<Order>>()

    // LiveData variable for the total order cost
    val orderCost = MutableLiveData<Double>()

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
}