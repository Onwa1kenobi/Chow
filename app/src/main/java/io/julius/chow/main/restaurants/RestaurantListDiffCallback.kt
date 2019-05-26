package io.julius.chow.main.restaurants

import androidx.recyclerview.widget.DiffUtil
import io.julius.chow.model.Restaurant

class RestaurantListDiffCallback(private val oldList: List<Restaurant>, private val newList: List<Restaurant>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].id == oldList[oldItemPosition].id
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
}