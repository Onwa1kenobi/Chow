package io.julius.chow.main.food

sealed class FoodViewContract {
    // Contract class to display message to the user
    class MessageDisplay(val message: String) : FoodViewContract()
}
