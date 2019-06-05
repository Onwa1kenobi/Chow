package io.julius.chow.model

import androidx.lifecycle.MutableLiveData

class Order(
    val food: Food,
    var quantity: Int = 1,
    val inHistory: Boolean = false
) {
    // Data binding live data variables for UI display
    val liveQuantity = MutableLiveData<Int>().apply { postValue(quantity) }
    val liveCost = MutableLiveData<Double>().apply {
        postValue((quantity * food.price))
//        postValue(DecimalFormat("0.00").apply {
//            roundingMode = RoundingMode.CEILING
//        }.format(quantity * food.price))
    }
}