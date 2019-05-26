package io.julius.chow.main.restaurants

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import io.julius.chow.R
import io.julius.chow.base.BaseAdapter
import io.julius.chow.model.Restaurant

class RestaurantsAdapter : BaseAdapter<Restaurant>() {

    private var restaurants: List<Restaurant> = ArrayList()

    var listener: (Restaurant, AppCompatImageView) -> Unit = { _, _ -> }

    override fun setData(data: List<Restaurant>) {
        val diffResult = DiffUtil.calculateDiff(RestaurantListDiffCallback(restaurants, data))
        this.restaurants = data
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemForPosition(position: Int): Restaurant {
        return this.restaurants[position]
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_restaurant
    }

    override fun getItemCount(): Int {
        return this.restaurants.size
    }

    fun onItemClick(restaurant: Restaurant, image: AppCompatImageView) {
        ViewCompat.setTransitionName(image, restaurant.id)
        listener(restaurant, image)
    }
}