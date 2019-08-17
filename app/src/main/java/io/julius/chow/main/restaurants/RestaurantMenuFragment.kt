package io.julius.chow.main.restaurants


import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentRestaurantMenuBinding
import io.julius.chow.main.food.FoodAdapter
import io.julius.chow.main.food.FoodDetailsFragment
import io.julius.chow.util.Event
import kotlinx.android.synthetic.main.fragment_restaurant_menu.*


class RestaurantMenuFragment : BaseFragment<FragmentRestaurantMenuBinding>() {

    override val contentResource = R.layout.fragment_restaurant_menu

    private lateinit var restaurantViewModel: RestaurantDetailsViewModel

    private val foodAdapter: FoodAdapter = FoodAdapter(FoodAdapter.MenuType.RestaurantMenu)

    override fun initialize(state: Bundle?) {
        appComponent.inject(this)

        //subscription to LiveData in RestaurantViewModel
        restaurantViewModel = viewModel(viewModelFactory) {
            observe(restaurantViewContract, ::viewStateResponse)
            observe(menu) {
                foodAdapter.setData(it)
            }
        }

        foodAdapter.listener = { food, image ->
            // Put restaurant id in bundle to fetch restaurant in detail view
            val bundle = bundleOf(FoodDetailsFragment.FOOD to food)
            // Put the restaurant image in an extra for shared element transition
            val extras = FragmentNavigatorExtras(
                image!! to food.id
            )
            // Navigate to detail view
            findNavController().navigate(R.id.action_menu_to_foodDetails, bundle, null, extras)
        }

        // Set recycler view adapter to restaurant adapter
        dataBinding.recyclerView.adapter = foodAdapter

        restaurantViewModel.getCurrentRestaurantMenu()
    }

    private fun viewStateResponse(event: Event<RestaurantViewContract>) {
        event.getContentIfNotHandled()?.let { data ->
            when (data) {
                is RestaurantViewContract.MessageDisplay -> {
                    // Display message feedback to user
                    notify(data.message)
                }

                is RestaurantViewContract.EmptyListDisplay -> {
                    empty_feed_view.visibility = if (data.display) View.VISIBLE
                    else View.GONE
                }
            }
        }
    }
}
