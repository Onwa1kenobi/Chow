package io.julius.chow.main.restaurants

sealed class RestaurantViewContract {
    // Contract class to display progress bar
    class ProgressDisplay(val display: Boolean) : RestaurantViewContract()

    // Contract class to display empty menu view
    class EmptyListDisplay(val display: Boolean) : RestaurantViewContract()

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : RestaurantViewContract()

    // Contract class to navigate to the SignUpFragment
    object NavigateToSignUp : RestaurantViewContract()

    // Contract class to navigate to the MainActivity
    object NavigateToHome : RestaurantViewContract()
}
