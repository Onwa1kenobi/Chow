package io.julius.chow.main.restaurants


import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentRestaurantsBinding
import io.julius.chow.model.Restaurant
import io.julius.chow.util.Event
import kotlinx.android.synthetic.main.fragment_restaurants.*


class RestaurantsFragment : BaseFragment<FragmentRestaurantsBinding>() {

    override val contentResource = R.layout.fragment_restaurants

    private lateinit var restaurantViewModel: RestaurantViewModel

    private val restaurantsAdapter: RestaurantsAdapter = RestaurantsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        //subscription to LiveData in RestaurantViewModel
        restaurantViewModel = viewModel(viewModelFactory) {
            observe(restaurantViewContract, ::viewStateResponse)
            observe(restaurants, ::updateRestaurants)
        }

        val transition = TransitionSet().setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(ChangeBounds())
            .addTransition(ChangeTransform())
            .addTransition(ChangeImageTransform())

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        enterTransition = Fade(Fade.MODE_IN)
        exitTransition = Fade(Fade.MODE_OUT)
    }

    override fun initialize(state: Bundle?) {

        // Prepare the restaurants adapter for item click listening
        restaurantsAdapter.listener = { restaurant, image ->
            // Put restaurant id in bundle to fetch restaurant in detail view
            val bundle = bundleOf(RESTAURANT to restaurant)
            // Put the restaurant image in an extra for shared element transition
            val extras = FragmentNavigatorExtras(
                image to restaurant.id
            )
            // Navigate to detail view
            findNavController().navigate(R.id.action_restaurants_to_restaurantDetails, bundle, null, extras)
        }

        // Set recycler view adapter to restaurant adapter
        dataBinding.recyclerView.adapter = restaurantsAdapter

        // Make call to view model to fetch restaurants
        restaurantViewModel.fetchRestaurants()
    }

    private fun viewStateResponse(event: Event<RestaurantViewContract>) {
        event.getContentIfNotHandled()?.let { data ->
            when (data) {
                is RestaurantViewContract.ProgressDisplay -> {
                    // Toggle progress bar visibility
                    if (data.display) progress_bar.visibility = View.VISIBLE else progress_bar.visibility =
                        View.INVISIBLE
                }

                is RestaurantViewContract.MessageDisplay -> {
                    // Display message feedback to user
                    notify(data.message)
                }
            }
        }
    }

    private fun updateRestaurants(restaurants: List<Restaurant>) {
        restaurantsAdapter.setData(restaurants)
    }

    companion object {
        const val RESTAURANT_ID = "restaurantId"
        const val RESTAURANT = "restaurant"
    }
}
