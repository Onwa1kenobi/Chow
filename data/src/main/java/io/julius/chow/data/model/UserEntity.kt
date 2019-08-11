package io.julius.chow.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "User")
class UserEntity() {

    @PrimaryKey
    lateinit var id: String
    lateinit var name: String
    lateinit var address: String
    lateinit var phoneNumber: String
    // Boolean variable to check if the profile was successfully created.
    var profileComplete: Boolean = false
    var isCurrentUser = false

    @Ignore
    constructor(
        id: String,
        name: String,
        address: String,
        phoneNumber: String,
        // Boolean variable to check if the profile was successfully created.
        profileComplete: Boolean = false
    ) : this() {
        this.id = id
        this.name = name
        this.address = address
        this.phoneNumber = phoneNumber
        this.profileComplete = profileComplete
    }
}