package io.julius.chow.main.food

sealed class FoodViewContract {
    // Contract class to display progress bar
    class ProgressDisplay(val display: Boolean) : FoodViewContract()

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : FoodViewContract()

    // Contract object to notify food save success
    object SaveSuccess : FoodViewContract()
}
