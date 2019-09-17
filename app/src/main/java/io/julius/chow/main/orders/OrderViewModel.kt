package io.julius.chow.main.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.chow.domain.Result
import io.julius.chow.domain.interactor.food.DeleteOrderInteractor
import io.julius.chow.domain.interactor.food.GetOrdersInteractor
import io.julius.chow.domain.interactor.food.PlaceOrderInteractor
import io.julius.chow.domain.interactor.food.SaveOrderInteractor
import io.julius.chow.domain.interactor.profile.GetUserInteractor
import io.julius.chow.domain.interactor.splash.UserPresentInteractor
import io.julius.chow.domain.model.OrderState
import io.julius.chow.domain.model.UserModel
import io.julius.chow.domain.model.UserType
import io.julius.chow.mapper.OrderMapper
import io.julius.chow.mapper.PlacedOrderMapper
import io.julius.chow.mapper.UserMapper
import io.julius.chow.model.Order
import io.julius.chow.model.PlacedOrder
import io.julius.chow.model.User
import io.julius.chow.util.Event
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class OrderViewModel @Inject constructor(
    private val getOrdersInteractor: GetOrdersInteractor,
    private val deleteOrderInteractor: DeleteOrderInteractor,
    private val getUserInteractor: GetUserInteractor,
    private val userPresentInteractor: UserPresentInteractor,
    private val placeOrderInteractor: PlaceOrderInteractor,
    private val saveOrderInteractor: SaveOrderInteractor
) : ViewModel() {

    // LiveData object for view state interaction
    val orderViewContract: MutableLiveData<Event<OrderViewContract>> = MutableLiveData()

    // LiveData object for order confirmation view state interaction
    val orderConfirmationViewContract: MutableLiveData<Event<OrderViewContract>> = MutableLiveData()

    // Public LiveData variable to expose returned list of orders
    val orders = MutableLiveData<List<Order>>()
    private var completeOrderList = listOf<Order>()

    // Restaurant order status filter
    var orderStatus = OrderState.ACTIVE

    // Current user variable to display user details
    val currentUser = MutableLiveData<User>()

    // Current logged user account type variable to query appropriate orders endpoint
    val currentAccountType = MutableLiveData<UserType?>()

    // variable for the total cost of orders
    val totalOrderCost: Double get() = subTotalOrderCost + tax + deliveryCharge

    // variable for the sub-total cost of orders
    var subTotalOrderCost: Double = 0.0

    // Tax calculated as 1%
    val tax: Double get() = subTotalOrderCost * (1 / 100.0)

    // Delivery cost simply calculated as 4% of total cost plus 200.
    // More complex method could use address distance and stuff.
    val deliveryCharge: Double get() = (subTotalOrderCost * (5 / 100.0)) + 150

    private val disposable = CompositeDisposable()

    fun getOrders(userType: UserType) {
        // Display progress bar
        orderViewContract.postValue(Event(OrderViewContract.ProgressDisplay(true)))

        getOrdersInteractor.execute(userType) {
            disposable.add(it.subscribe({ result ->
                // Hide progress bar
                orderViewContract.postValue(Event(OrderViewContract.ProgressDisplay(false)))

                when (result) {
                    is Result.Success -> {
                        // Map all order model objects and post the list
                        val response = result.data.map { orderModel ->
                            OrderMapper.mapFromModel(orderModel)
                        }

                        completeOrderList = response
                        filterOrders(orderStatus)
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
            }))
        }
    }

    fun removeOrder(order: Order) {
        deleteOrderInteractor.execute(OrderMapper.mapToModel(order))
    }

    fun getCurrentAccountType() {
        userPresentInteractor.execute(UserPresentInteractor.Params(UserPresentInteractor.Params.ParamType.GET_CURRENT_ACCOUNT_TYPE)) {
            when (it) {
                is Result.Success<*> -> {
                    when (it.data) {
                        is UserType -> currentAccountType.postValue(it.data as UserType)
                        else -> currentAccountType.postValue(null)
                    }
                }
                else -> currentAccountType.postValue(null)
            }
        }
    }

    fun getCurrentUser() {
        getUserInteractor.execute(false) {
            currentUser.postValue(UserMapper.mapFromModel(it as UserModel))
        }
    }

    fun placeOrder(address: String, deliveryTime: String) {
        val placedOrder = PlacedOrder(
            id = "",
            orders = orders.value!!,
            user = currentUser.value!!.apply { this.address = address },
            createdAt = Calendar.getInstance().time,
            deliveryTime = deliveryTime,
            subTotalCost = subTotalOrderCost,
            tax = tax,
            deliveryCharge = deliveryCharge
        )

        // Display progress bar
        orderConfirmationViewContract.postValue(Event(OrderViewContract.ProgressDisplay(true)))

        placeOrderInteractor.execute(PlacedOrderMapper.mapToModel(placedOrder)) {
            // Hide progress bar
            orderConfirmationViewContract.postValue(Event(OrderViewContract.ProgressDisplay(false)))

            // Display returned message to user
            orderConfirmationViewContract.postValue(
                Event(OrderViewContract.MessageDisplay(it))
            )
        }
    }

    fun filterOrders(filter: OrderState) {
        orders.postValue(completeOrderList.filter { it.status == filter })
    }

    fun updateOrder(order: Order) {
        saveOrderInteractor.execute(OrderMapper.mapToModel(order))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}