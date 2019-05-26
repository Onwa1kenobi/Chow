package io.julius.chow.di

import dagger.Component
import io.julius.chow.app.App
import io.julius.chow.auth.AuthActivity
import io.julius.chow.di.viewmodel.ViewModelModule
import io.julius.chow.main.food.FoodDetailsFragment
import io.julius.chow.main.orders.OrdersFragment
import io.julius.chow.main.profile.ProfileFragment
import io.julius.chow.main.restaurants.RestaurantDetailsFragment
import io.julius.chow.main.restaurants.RestaurantsFragment
import io.julius.chow.splash.SplashActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(app: App)

    fun inject(splashActivity: SplashActivity)

    fun inject(authActivity: AuthActivity)

    fun inject(restaurantsFragment: RestaurantsFragment)

    fun inject(ordersFragment: OrdersFragment)

    fun inject(profileFragment: ProfileFragment)

    fun inject(restaurantDetailsFragment: RestaurantDetailsFragment)

    fun inject(foodDetailsFragment: FoodDetailsFragment)
}