package io.julius.chow.main.menu

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentMenuBinding
import io.julius.chow.main.food.FoodAdapter
import io.julius.chow.main.food.FoodDetailsFragment
import io.julius.chow.main.restaurants.RestaurantViewContract
import io.julius.chow.util.Event
import kotlinx.android.synthetic.main.fragment_menu.*
import java.util.*

class MenuFragment : BaseFragment<FragmentMenuBinding>(), View.OnClickListener {

    override val contentResource = R.layout.fragment_menu

    private lateinit var menuViewModel: MenuViewModel

    private val foodAdapter: FoodAdapter = FoodAdapter(FoodAdapter.MenuType.MainMenu)

    private val menuCategories = mutableListOf<TextView>()

    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        //subscription to LiveData in MenuViewModel
        menuViewModel = viewModel(viewModelFactory) {
            observe(restaurantViewContract, ::viewStateResponse)
            observe(menu) {
                foodAdapter.setData(it)
            }
        }
    }

    override fun initialize(state: Bundle?) {
        dataBinding.clickListener = this

        menuCategories.addAll(listOf(label_breakfast, label_lunch, label_dinner))

        initialiseCategory(Calendar.getInstance())

        // Prepare the restaurants adapter for item click listening
        foodAdapter.listener = { food, image ->
            //            if (image == null) {
//                // From our design, if the image is null, then the click operation was to add the current food to order
////                menuViewModel.addOrder(food)
//            } else {
            // Put restaurant id in bundle to fetch restaurant in detail view
            val bundle = bundleOf(FoodDetailsFragment.FOOD to food)
            // Put the restaurant image in an extra for shared element transition
            val extras = FragmentNavigatorExtras(
                image!! to food.id
            )
            // Navigate to detail view
            findNavController().navigate(R.id.action_menu_to_foodDetails, bundle, null, extras)
        }
//        }

        // Set recycler view adapter to restaurant adapter
        dataBinding.recyclerView.adapter = foodAdapter

        // Make call to view model to fetch category menu
        menuViewModel.fetchCategoryMenu(category)
    }

    private fun initialiseCategory(currentTime: Calendar) {
        val tenAM = Calendar.getInstance()
        tenAM.set(Calendar.HOUR_OF_DAY, 10)

        val onePM = Calendar.getInstance()
        onePM.set(Calendar.HOUR_OF_DAY, 13)

        val fivePM = Calendar.getInstance()
        fivePM.set(Calendar.HOUR_OF_DAY, 17)

        category = when {
            currentTime.after(onePM) -> {
                label_dinner.isSelected = true
                "dinner"
            }

            currentTime.after(tenAM) -> {
                label_lunch.isSelected = true
                "lunch"
            }

            else -> {
                label_breakfast.isSelected = true
                "breakfast"
            }
        }
    }

    private fun viewStateResponse(event: Event<RestaurantViewContract>) {
        event.getContentIfNotHandled()?.let { data ->
            when (data) {
                is RestaurantViewContract.MessageDisplay -> {
                    // Display message feedback to user
                    notify(data.message)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            for (category in menuCategories) {
                category.isSelected = category == v
                if (category.isSelected) {
                    this.category = category.text.toString().toLowerCase()
                    menuViewModel.fetchCategoryMenu(this.category)
                }
            }
        }
    }
}
