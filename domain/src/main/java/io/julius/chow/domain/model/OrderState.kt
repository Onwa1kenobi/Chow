package io.julius.chow.domain.model

import java.util.*

enum class OrderState(val value: String) {
    ACTIVE("active"), PROCESSED("processed"), REJECTED("rejected");

    companion object {
        fun getOrderState(value: String): OrderState {
            return when (value.toLowerCase(Locale.getDefault())) {
                PROCESSED.value -> PROCESSED
                REJECTED.value -> REJECTED
                else -> ACTIVE
            }
        }
    }
}