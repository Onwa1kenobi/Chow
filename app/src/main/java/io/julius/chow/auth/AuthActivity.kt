package io.julius.chow.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
    }
}
