package io.julius.chow.main.food

import androidx.recyclerview.widget.DiffUtil
import io.julius.chow.model.Food
import io.julius.chow.model.Restaurant

class FoodListDiffCallback(private val oldList: List<Food>, private val newList: List<Food>) :
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