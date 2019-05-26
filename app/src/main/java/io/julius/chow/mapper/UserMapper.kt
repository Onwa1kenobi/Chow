package io.julius.chow.mapper

import io.julius.chow.domain.model.UserModel
import io.julius.chow.model.User

/**
 * Map a [User] to and from a [UserModel] instance when data is moving between
 * this layer and the Domain layer
 */
object UserMapper : Mapper<User, UserModel> {

    /**
     * Map a [User] instance to a [UserModel] instance
     */
    override fun mapToModel(type: User): UserModel {
        return UserModel(
            id = type.id,
            name = type.name,
            address = type.address,
            phoneNumber = type.phoneNumber,
            profileComplete = type.profileComplete
        )
    }

    /**
     * Map an instance of a [UserModel] to a [User] instance
     */
    override fun mapFromModel(type: UserModel): User {
        return User(
            id = type.id,
            name = type.name,
            address = type.address,
            phoneNumber = type.phoneNumber,
            profileComplete = type.profileComplete
        )
    }
}