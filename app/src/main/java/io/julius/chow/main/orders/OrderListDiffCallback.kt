package io.julius.chow.main.orders

import androidx.recyclerview.widget.DiffUtil
import io.julius.chow.model.Order

class OrderListDiffCallback(private val oldList: List<Order>, private val newList: List<Order>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].food.id == oldList[oldItemPosition].food.id
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
}