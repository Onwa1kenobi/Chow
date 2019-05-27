package io.julius.chow.main.orders

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import io.julius.chow.R
import io.julius.chow.base.BaseAdapter
import io.julius.chow.model.Order

class OrderAdapter(private val lifecycleOwner: OrdersFragment) : BaseAdapter<Order>() {

    private var orders: List<Order> = ArrayList()

    var listener: (Order, AppCompatImageView?) -> Unit = { _, _ -> }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // If the binding does not have a lifecycle owner, set a new one with the fragment passed when creating this adapter
        if (binding.lifecycleOwner == null) {
            binding.lifecycleOwner = lifecycleOwner
        }
        super.onBindViewHolder(holder, position)
    }

    override fun setData(data: List<Order>) {
        val diffResult = DiffUtil.calculateDiff(OrderListDiffCallback(orders, data))
        this.orders = data
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemForPosition(position: Int): Order {
        return this.orders[position]
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_order_layout
    }

    override fun getItemCount(): Int {
        return this.orders.size
    }

    fun onItemClick(order: Order, image: AppCompatImageView) {
        ViewCompat.setTransitionName(image, order.food.id)
        listener(order, image)
    }

    fun onRemoveOrder(order: Order) {
        listener(order, null)
    }

    fun incrementOrderQuantity(order: Order) {
        // Set maximum order limit to 50
        if (order.quantity < 50) {
            order.quantity += 1
        }
        order.liveQuantity.postValue(order.quantity)
        order.liveCost.postValue(order.quantity * order.food.price)
    }

    fun decrementOrderQuantity(order: Order) {
        orders.indexOf(order)
        // Set minimum order limit to 1
        if (order.quantity > 1) {
            order.quantity -= 1
        }
        order.liveQuantity.postValue(order.quantity)
        order.liveCost.postValue(order.quantity * order.food.price)
    }
}