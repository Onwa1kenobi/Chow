package io.julius.chow.auth


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import io.julius.chow.R
import io.julius.chow.base.extension.observe
import io.julius.chow.util.Event
import kotlinx.android.synthetic.main.fragment_auth.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class AuthFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_phone_login.setOnClickListener {
            initiatePhoneAuth()
        }

        observe(authViewModel.authContractData) {
            it.getContentIfNotHandled()?.let { data ->
                when (data) {
                    is AuthViewContract.ProgressDisplay -> {
                        if (data.display) progress_bar.visibility = View.VISIBLE else progress_bar.visibility =
                            View.INVISIBLE

                        // Enable/Disable data views
                        button_phone_login.isEnabled = !data.display
                    }

                    is AuthViewContract.MessageDisplay -> {
                        Toast.makeText(context, data.message, Toast.LENGTH_SHORT).show()
                    }

                    is AuthViewContract.NavigateToSignUp -> {
                        Navigation.findNavController(activity!!, R.id.navigation_host_fragment)
                            .navigate(R.id.action_signUpDetailsFragment)
                    }

                    is AuthViewContract.NavigateToHome -> {
                        // Navigate to the MainActivity and finish this current activity
                        Navigation.findNavController(activity!!, R.id.navigation_host_fragment)
                            .navigate(R.id.action_authFragment_to_mainActivity)
                        activity?.finish()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Hide progress bar
        authViewModel.authContractData.value = Event(AuthViewContract.ProgressDisplay(false))

        if (resultCode != RESULT_OK) {
            Toast.makeText(context, "Authentication failed. Try again.", Toast.LENGTH_SHORT).show()
            return
        }

        when (requestCode) {
            RC_PHONE_SIGN_IN -> {
                authViewModel.authCurrentUser()
            }
        }
    }

    private fun initiatePhoneAuth() {
        // Display progress bar
        authViewModel.authContractData.value = Event(AuthViewContract.ProgressDisplay(true))

        // Choose authentication providers
        val providers = Arrays.asList(
            AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher)
                .build(),
            RC_PHONE_SIGN_IN
        )
    }

    companion object {
        private const val RC_PHONE_SIGN_IN = 123
    }
}
