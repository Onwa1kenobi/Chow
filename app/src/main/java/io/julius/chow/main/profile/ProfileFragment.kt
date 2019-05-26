package io.julius.chow.main.profile


import android.os.Bundle
import android.view.View
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentProfileBinding
import io.julius.chow.util.Event

class ProfileFragment : BaseFragment<FragmentProfileBinding>(), View.OnClickListener {

    override val contentResource = R.layout.fragment_profile

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        //subscription to LiveData in RestaurantViewModel
        profileViewModel = viewModel(viewModelFactory) {
            observe(user) {
                dataBinding.user = it
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
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v) {
                dataBinding.buttonEditAddress -> {

                }

                dataBinding.buttonHistoryDetails -> {

                }
            }
        }
    }
}
