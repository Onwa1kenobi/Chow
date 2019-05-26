package io.julius.chow.main.food

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import io.julius.chow.R
import io.julius.chow.base.BaseAdapter
import io.julius.chow.model.Food

class FoodAdapter : BaseAdapter<Food>() {

    private var foodList: List<Food> = ArrayList()

    var listener: (Food, AppCompatImageView?) -> Unit = { _, _ -> }

    override fun setData(data: List<Food>) {
        val diffResult = DiffUtil.calculateDiff(FoodListDiffCallback(foodList, data))
        this.foodList = data
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemForPosition(position: Int): Food {
        return this.foodList[position]
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_restaurant_food_layout
    }

    override fun getItemCount(): Int {
        return this.foodList.size
    }

    fun onItemClick(food: Food, image: AppCompatImageView) {
        ViewCompat.setTransitionName(image, food.id)
        listener(food, image)
    }

    fun onAddCartClick(food: Food) {
        listener(food, null)
    }
}