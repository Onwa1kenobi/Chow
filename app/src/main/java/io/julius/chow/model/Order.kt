package io.julius.chow.model

import androidx.lifecycle.MutableLiveData
import java.math.RoundingMode
import java.text.DecimalFormat

class Order(
    val food: Food,
    var quantity: Int = 1
) {
    // Data binding live data variables for UI display
    val liveQuantity = MutableLiveData<Int>().apply { postValue(quantity) }
    val liveCost = MutableLiveData<String>().apply {
        postValue(DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.CEILING
        }.format(quantity * food.price))
    }
}