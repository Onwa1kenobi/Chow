package io.julius.chow.model

import androidx.lifecycle.MutableLiveData
import io.julius.chow.domain.model.OrderState

class Order(
    val food: Food,
    var quantity: Int = 1,
    var status: OrderState = OrderState.ACTIVE,
    val inHistory: Boolean = false
) {
    // Data binding live data variables for UI display
    val liveQuantity = MutableLiveData<Int>().apply { postValue(quantity) }
    val liveCost = MutableLiveData<Double>().apply {
        postValue((quantity * food.price))
    }
}