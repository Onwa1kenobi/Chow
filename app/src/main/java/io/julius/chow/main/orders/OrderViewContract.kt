package io.julius.chow.main.orders

sealed class OrderViewContract {
    // Contract class to display progress bar
    class ProgressDisplay(val display: Boolean) : OrderViewContract()

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : OrderViewContract()

    // Contract object to navigate to the FoodDetailsFragment
    object NavigateToFoodDetails : OrderViewContract()
}
