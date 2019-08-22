package io.julius.chow.main.restaurants


import android.content.Intent
import android.os.Bundle
import android.view.View
import io.julius.chow.R
import io.julius.chow.auth.AuthActivity
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentRestaurantProfileBinding
import io.julius.chow.main.profile.ProfileViewContract
import io.julius.chow.util.Event


class RestaurantProfileFragment : BaseFragment<FragmentRestaurantProfileBinding>(), View.OnClickListener {

    override val contentResource = R.layout.fragment_restaurant_profile

    private lateinit var profileViewModel: RestaurantProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        //subscription to LiveData in RestaurantViewModel
        profileViewModel = viewModel(viewModelFactory) {
            observe(restaurant) {
                dataBinding.restaurant = it
            }
            observe(profileViewContract, ::viewStateResponse)
        }
    }

    override fun initialize(state: Bundle?) {
        dataBinding.clickListener = this
    }

    private fun viewStateResponse(event: Event<ProfileViewContract>) {
        event.getContentIfNotHandled()?.let { data ->
            when (data) {
                is ProfileViewContract.MessageDisplay -> {
                    // Display message feedback to user
                    notify(data.message)
                }

                is ProfileViewContract.ProgressDisplay -> {
                    // Display progress bar
                }

                is ProfileViewContract.NavigateToHistory -> {
                    // Navigate to history fragment
                }

                is ProfileViewContract.NavigateToAuth -> {
                    // Navigate to auth activity
                    val intent = Intent(context, AuthActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v) {
                dataBinding.buttonSignOut -> {
                    profileViewModel.signOut()
                }
            }
        }
    }
}
