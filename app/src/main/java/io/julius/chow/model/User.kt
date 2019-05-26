package io.julius.chow.model

data class User(
    val id: String,
    var name: String,
    var address: String,
    val phoneNumber: String,
    // Boolean variable to check if the profile was completely created successfully.
    var profileComplete: Boolean = false
)