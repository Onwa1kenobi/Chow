package io.julius.chow.main.orders

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import io.julius.chow.R
import io.julius.chow.base.BaseAdapter
import io.julius.chow.model.Order

class OrderAdapter(private val lifecycleOwner: OrdersFragment) : BaseAdapter<Order>() {

    private var orders: List<Order> = ArrayList()

    var listener: (Order, AppCompatImageView?) -> Unit = { _, _ -> }

    // LiveData variable for the total order cost
    val totalOrderCost = MutableLiveData<Double>().apply {
        // Post initial value of zero
        postValue(0.0)
    }

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

        // Update total cost
        updateTotalCost()
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

            order.liveQuantity.value = (order.quantity)
            order.liveCost.value = (order.quantity * order.food.price)

            // Update total cost
            updateTotalCost()
        }
    }

    fun decrementOrderQuantity(order: Order) {
        // Set minimum order limit to 1
        if (order.quantity > 1) {
            order.quantity -= 1

            order.liveQuantity.value = (order.quantity)
            order.liveCost.value = (order.quantity * order.food.price)

            // Update total cost
            updateTotalCost()
        }
    }

    private fun updateTotalCost() {
        totalOrderCost.apply {
            var totalCost = 0.0

            orders.forEach { order ->
                // Force unwrap was giving NullPointerException for some reason.
                order.liveCost.value?.let {
                    totalCost += it
                }
            }

            value = totalCost
        }
    }
}