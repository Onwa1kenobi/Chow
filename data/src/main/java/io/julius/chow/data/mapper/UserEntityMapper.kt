package io.julius.chow.data.mapper

import io.julius.chow.data.model.UserEntity
import io.julius.chow.domain.model.UserModel

/**
 * Map a [UserEntity] to and from a [UserModel] instance when data is moving between
 * this layer and the Domain layer
 */
object UserEntityMapper : Mapper<UserEntity, UserModel> {

    /**
     * Map a [UserEntity] instance to a [UserModel] instance
     */
    override fun mapFromEntity(type: UserEntity): UserModel {

        return UserModel(
            id = type.id,
            name = type.name,
            address = type.address,
            phoneNumber = type.phoneNumber,
            profileComplete = type.profileComplete
        )
    }

    /**
     * Map an instance of a [UserModel] to a [UserEntity] model
     */
    override fun mapToEntity(type: UserModel): UserEntity {
        return UserEntity(
            id = type.id,
            name = type.name,
            address = type.address,
            phoneNumber = type.phoneNumber,
            profileComplete = type.profileComplete
        )
    }
}