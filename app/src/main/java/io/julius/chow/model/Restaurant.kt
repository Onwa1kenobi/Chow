package io.julius.chow.model

import android.os.Parcel
import android.os.Parcelable

data class Restaurant(
    val id: String,
    var name: String,
    var imageUrl: String,
    val phoneNumber: String,
    var description: String,
    var address: String,
    var emailAddress: String,
    var location: String,
    var locationLongitude: Double,
    var locationLatitude: Double,
    var profileComplete: Boolean = false
) : Parcelable {

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeString(id)
            it.writeString(name)
            it.writeString(imageUrl)
            it.writeString(phoneNumber)
            it.writeString(description)
            it.writeString(address)
            it.writeString(emailAddress)
            it.writeString(location)
            it.writeDouble(locationLongitude)
            it.writeDouble(locationLatitude)
            it.writeInt(if (profileComplete) 1 else 0)
        }
    }

    constructor(internal: Parcel) : this(
        id = internal.readString().orEmpty(),
        name = internal.readString().orEmpty(),
        imageUrl = internal.readString().orEmpty(),
        phoneNumber = internal.readString().orEmpty(),
        description = internal.readString().orEmpty(),
        address = internal.readString().orEmpty(),
        emailAddress = internal.readString().orEmpty(),
        location = internal.readString().orEmpty(),
        locationLongitude = internal.readDouble(),
        locationLatitude = internal.readDouble(),
        profileComplete = internal.readInt() == 1
    )
//    {
//        menu = internal.createStringArrayList()
//    }

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }
}