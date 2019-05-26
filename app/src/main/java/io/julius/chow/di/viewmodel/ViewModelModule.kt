package io.julius.chow.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.julius.chow.auth.AuthViewModel
import io.julius.chow.main.food.FoodDetailsViewModel
import io.julius.chow.main.orders.OrderViewModel
import io.julius.chow.main.profile.ProfileViewModel
import io.julius.chow.main.restaurants.RestaurantDetailsViewModel
import io.julius.chow.main.restaurants.RestaurantViewModel
import io.julius.chow.splash.SplashViewModel

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindsSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindsAuthViewModel(authViewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RestaurantViewModel::class)
    abstract fun bindsRestaurantViewModel(restaurantViewModel: RestaurantViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderViewModel::class)
    abstract fun bindsOrderViewModel(orderViewModel: OrderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindsProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RestaurantDetailsViewModel::class)
    abstract fun bindsRestaurantDetailsViewModel(restaurantDetailsViewModel: RestaurantDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FoodDetailsViewModel::class)
    abstract fun bindsFoodDetailsViewModel(foodDetailsViewModel: FoodDetailsViewModel): ViewModel
}