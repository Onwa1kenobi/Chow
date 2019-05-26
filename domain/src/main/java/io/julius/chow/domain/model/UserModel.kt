package io.julius.chow.domain.model

class UserModel(
    val id: String,
    val name: String,
    var address: String,
    val phoneNumber: String,
    // Boolean variable to check if the profile was completely created successfully.
    val profileComplete: Boolean
)