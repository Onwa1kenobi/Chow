package io.julius.chow.auth

sealed class AuthViewContract {
    // Contract class to display progress bar
    class ProgressDisplay(val display: Boolean) : AuthViewContract()

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : AuthViewContract()

    // Contract class to navigate to the SignUpFragment
    object NavigateToSignUp : AuthViewContract()

    // Contract class to navigate to the MainActivity
    object NavigateToHome : AuthViewContract()
}
