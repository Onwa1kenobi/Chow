package io.julius.chow.data.source.cache.converter

import androidx.room.TypeConverter
import io.julius.chow.domain.model.OrderState
import java.util.*

class OrderStateConverter {
    @TypeConverter
    fun fromString(value: String?): OrderState? {
        return value?.let { OrderState.valueOf(it.toLowerCase(Locale.getDefault())) }
    }

    @TypeConverter
    fun stateToString(status: OrderState?): String? {
        return status?.value
    }
}