package io.julius.chow.main.food

import android.os.Bundle
import android.view.View
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
import io.julius.chow.model.Food

class FoodDetailsFragment : BaseFragment<FragmentFoodDetailsBinding>(), View.OnClickListener {

    override val contentResource = R.layout.fragment_food_details

    private lateinit var foodDetailsViewModel: FoodDetailsViewModel

    private lateinit var food: Food

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
        foodDetailsViewModel.createOrder(food)

//        postponeEnterTransition()
    }

    override fun initialize(state: Bundle?) {

        ViewCompat.setTransitionName(dataBinding.imageFoodBanner, food.id)

        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = foodDetailsViewModel
        dataBinding.clickListener = this

//        startPostponedEnterTransition()

    }

    override fun onClick(v: View?) {
        findNavController().popBackStack()
    }

    companion object {
        const val FOOD = "food"
    }
}
