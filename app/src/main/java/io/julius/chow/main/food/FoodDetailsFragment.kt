package io.julius.chow.main.food

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.Fade
import androidx.transition.TransitionSet
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentFoodDetailsBinding
import io.julius.chow.domain.model.UserType
import io.julius.chow.model.Food
import kotlinx.android.synthetic.main.fragment_food_details.*

class FoodDetailsFragment : BaseFragment<FragmentFoodDetailsBinding>(), View.OnClickListener {

    override val contentResource = R.layout.fragment_food_details

    private lateinit var foodDetailsViewModel: FoodDetailsViewModel

    private lateinit var food: Food
    private lateinit var userType: UserType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val transition = TransitionSet().setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(ChangeBounds())
            .addTransition(ChangeTransform())

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        enterTransition = Fade(Fade.MODE_IN)
        exitTransition = Fade(Fade.MODE_OUT)

        val safeArgs: FoodDetailsFragmentArgs by navArgs()
        food = safeArgs.food
        userType = safeArgs.userType

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        //subscription to LiveData in FoodDetailsViewModel
        foodDetailsViewModel = viewModel(viewModelFactory) {
            observe(foodViewContract) { event ->
                event.getContentIfNotHandled()?.let { data ->
                    when (data) {
                        is FoodViewContract.MessageDisplay -> {
                            // Display message feedback to user
                            notify(data.message)
                        }
                    }
                }
            }
        }
        foodDetailsViewModel.prepareOrder(food)

        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userType == UserType.RESTAURANT) {
            order_unit_container.visibility = View.GONE
            button_add_order.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_edit)
            button_add_order.text = resources.getString(R.string.edit)
        }

        button_add_order.setOnClickListener {
            if (userType == UserType.RESTAURANT) {
                val bundle = bundleOf(
                    FOOD to food
                )
                // Navigate to detail view
                findNavController().navigate(R.id.action_foodDetails_to_addFood, bundle)
            } else {
                foodDetailsViewModel.addOrder()
            }
        }
    }

    override fun initialize(state: Bundle?) {

        ViewCompat.setTransitionName(dataBinding.imageFoodBanner, food.id)

        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = foodDetailsViewModel
        dataBinding.clickListener = this

        startPostponedEnterTransition()

    }

    override fun onClick(v: View?) {
        findNavController().popBackStack()
    }

    companion object {
        const val FOOD = "food"
        const val USER_TYPE = "userType"
    }
}
