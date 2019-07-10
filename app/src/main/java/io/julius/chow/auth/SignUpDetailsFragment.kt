package io.julius.chow.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.julius.chow.R
import io.julius.chow.base.extension.observe
import kotlinx.android.synthetic.main.fragment_sign_up_details.*

/**
 * A simple [Fragment] subclass.
 *
 */
class SignUpDetailsFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)

        val safeArgs: SignUpDetailsFragmentArgs by navArgs()
        isEditMode = safeArgs.isEditMode
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEditMode) {
            // Fetch the current user details
            authViewModel.getCurrentUser()

            // Change toolbar title to reflect the edit mode action
            toolbar.title = "Update Information"

            // Set back button to finish the activity
            toolbar.setNavigationOnClickListener {
                activity?.finish()
            }
        } else {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        button_done.setOnClickListener {
            layout_name_field_wrapper.isErrorEnabled = false
            layout_address_field_wrapper.isErrorEnabled = false
            when {
                field_user_name.text.toString().trim().isEmpty() -> layout_name_field_wrapper.error =
                    "Please enter your name"
                field_user_address.text.toString().trim().isEmpty() -> layout_address_field_wrapper.error =
                    "Please enter your address"
                else -> authViewModel.completeUserRegistration(
                    field_user_name.text.toString().trim(),
                    field_user_address.text.toString().trim()
                )
            }
        }

        observe(authViewModel.authContractData) {
            it?.getContentIfNotHandled()?.let { data ->
                when (data) {
                    is AuthViewContract.ProgressDisplay -> {
                        if (data.display) progress_bar.visibility = View.VISIBLE else progress_bar.visibility =
                            View.INVISIBLE

                        // Enable/Disable data views
                        button_done.isEnabled = !data.display
                        field_user_name.isEnabled = !data.display
                        field_user_address.isEnabled = !data.display
                    }

                    is AuthViewContract.MessageDisplay -> {
                        layout_name_field_wrapper.error = data.message
                    }

                    is AuthViewContract.NavigateToHome -> {
                        // If this fragment was not entered to edit user info, navigate to main activity;
                        // else, finish the current activity
                        if (!isEditMode) {
                            // Navigate to the MainActivity and finish this current activity
                            Navigation.findNavController(activity!!, R.id.navigation_host_fragment)
                                .navigate(R.id.action_mainActivity)
                        }
                        activity?.finish()
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }

        observe(authViewModel.currentUser) {
            field_user_name.setText(it.name)
            field_user_address.setText(it.address)
        }
    }

    companion object {
        const val EDIT_MODE = "isEditMode"
    }
}
