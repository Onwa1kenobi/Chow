package io.julius.chow.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.navArgs
import io.julius.chow.R
import io.julius.chow.app.App
import javax.inject.Inject


class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject required objects for this activity (viewModelFactory)
        (application as App).appComponent.inject(this)

        // Create view model with activity scope so that hosted fragment will have access to the same instance
        ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel::class.java)

        // Set content view after creating ViewModel since we're using Navigation Component
        setContentView(R.layout.activity_auth)

        // Verify that this activity was indeed started with an intent extra passed
        // This is false when signing up as the SplashActivity does not use
        // the Navigation component to start this activity.
        if (intent.extras != null) {
            // Check if this activity was called to edit user info
            val safeArguments: AuthActivityArgs? by navArgs()
            safeArguments?.let { safeArgs ->
                if (safeArgs.isEditMode) {
                    // Set edit mode to true for destination fragment
                    val bundle = bundleOf(SignUpDetailsFragment.EDIT_MODE to true)

                    // Build NavOptions and popUp all fragments before the one we want so that we don't have a backstack
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.authFragment, true)
                        .build()

                    // Navigate to user details edit fragment
                    findNavController(this, R.id.navigation_host_fragment)
                        .navigate(R.id.action_signUpDetailsFragment, bundle, navOptions)
                }
            }
        }
    }
}
