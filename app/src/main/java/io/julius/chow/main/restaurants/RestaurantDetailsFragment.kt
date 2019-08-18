package io.julius.chow.main.restaurants


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.Fade
import androidx.transition.TransitionSet
import androidx.transition.TransitionSet.ORDERING_TOGETHER
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentRestaurantDetailsBinding
import io.julius.chow.main.food.FoodAdapter
import io.julius.chow.main.food.FoodDetailsFragment.Companion.FOOD
import io.julius.chow.model.Restaurant
import io.julius.chow.util.Event


class RestaurantDetailsFragment : BaseFragment<FragmentRestaurantDetailsBinding>(), View.OnClickListener {

    override val contentResource = R.layout.fragment_restaurant_details

    private lateinit var restaurantDetailsViewModel: RestaurantDetailsViewModel

    private lateinit var restaurant: Restaurant

    private val foodAdapter: FoodAdapter = FoodAdapter(FoodAdapter.MenuType.RestaurantMenuExtended)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()

        val transition = TransitionSet().setOrdering(ORDERING_TOGETHER)
            .addTransition(ChangeBounds())
            .addTransition(ChangeTransform())

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        enterTransition = Fade(Fade.MODE_IN)
        exitTransition = Fade(Fade.MODE_OUT)

        val safeArgs: RestaurantDetailsFragmentArgs by navArgs()
        restaurant = safeArgs.restaurant

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        //subscription to LiveData in RestaurantViewModel
        restaurantDetailsViewModel = viewModel(viewModelFactory) {
            observe(restaurantViewContract, ::viewStateResponse)
            observe(menu) {
                dataBinding.menu = it
                foodAdapter.setData(it)
            }
        }
    }

    override fun initialize(state: Bundle?) {

        ViewCompat.setTransitionName(dataBinding.imageRestaurantBanner, restaurant.id)

        dataBinding.restaurant = restaurant
        dataBinding.clickListener = this

        startPostponedEnterTransition()

        // Prepare the restaurants adapter for item click listening
        foodAdapter.listener = { food, image ->
            if (image == null) {
                // From our design, if the image is null, then the click operation was to add the current food to order
                restaurantDetailsViewModel.addOrder(food)
            } else {
                // Put food object in bundle to display in detail view
                val bundle = bundleOf(FOOD to food)
                // Put the restaurant image in an extra for shared element transition
                val extras = FragmentNavigatorExtras(
                    image to food.id
                )
                // Navigate to detail view
                findNavController().navigate(R.id.action_restaurantDetails_to_foodDetails, bundle, null, extras)
            }
        }

        // Set recycler view adapter to restaurant adapter
        dataBinding.recyclerViewRestaurantMenu.adapter = foodAdapter

        // Make call to view model to fetch restaurant menu
        restaurantDetailsViewModel.fetchRestaurantMenu(restaurant.id)
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
            when (v) {
                dataBinding.buttonCall -> {
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + restaurant.phoneNumber)
                    )
                    startActivity(intent)
                }

                dataBinding.buttonDirections -> {
                    val location = ("https://www.google.com/maps/dir/?api=1&destination="
                            + restaurant.locationLatitude
                            + "%2C"
                            + restaurant.locationLongitude)

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(location))
                    startActivity(intent)
                }

                else -> findNavController().popBackStack()
            }
        }
    }
}
