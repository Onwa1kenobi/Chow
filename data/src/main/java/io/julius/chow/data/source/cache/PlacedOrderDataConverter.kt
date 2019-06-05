package io.julius.chow.data.source.cache

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.julius.chow.data.model.OrderEntity

class PlacedOrderDataConverter {

    @TypeConverter
    fun fromOrderEntityList(value: List<OrderEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<OrderEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOrderEntityList(value: String): List<OrderEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<OrderEntity>>() {}.type
        return gson.fromJson(value, type)
    }
}