package io.julius.chow.main.profile

sealed class ProfileViewContract {
    // Contract class to display progress bar
    class ProgressDisplay(val display: Boolean) : ProfileViewContract()

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : ProfileViewContract()

    // Contract class to navigate to the History
    object NavigateToHistory : ProfileViewContract()
}
