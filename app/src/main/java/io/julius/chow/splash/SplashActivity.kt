package io.julius.chow.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.julius.chow.app.App
import io.julius.chow.auth.AuthActivity
import io.julius.chow.main.MainActivity
import io.julius.chow.main.restaurants.RestaurantMainActivity
import io.julius.chow.model.Restaurant
import io.julius.chow.model.User
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject required objects for this activity (viewModelFactory)
        (application as App).appComponent.inject(this)

        // Get a reference to the SplashViewModel
        val splashViewModel = ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel::class.java)

        // Observe changes to the userLoggedIn boolean status and check if there is a user currently logged in
        splashViewModel.userIsLoggedIn.observe(this, Observer { userIsLoggedIn ->

            val intent = if (userIsLoggedIn) {
                // User is logged in, check the user type and proceed to respective MainActivity
                when (splashViewModel.getCurrentLoggedUser()) {
                    is User -> Intent(this, MainActivity::class.java)
                    is Restaurant -> Intent(this, RestaurantMainActivity::class.java)
                    else -> Intent(this, AuthActivity::class.java)
                }
            } else {
                // User is not logged in, move to AuthActivity
                Intent(this, AuthActivity::class.java)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })
    }
}
