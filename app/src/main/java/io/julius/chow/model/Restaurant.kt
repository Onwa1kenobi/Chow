package io.julius.chow.model

import android.os.Parcel
import android.os.Parcelable

data class Restaurant(
    val id: String,
    var name: String,
    var imageUrl: String,
    val phoneNumber: String,
    val description: String,
    val address: String,
    val location: String,
    val locationLongitude: Double,
    val locationLatitude: Double
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
            it.writeString(location)
            it.writeDouble(locationLongitude)
            it.writeDouble(locationLatitude)
        }
    }

    constructor(internal: Parcel) : this(
        id = internal.readString(),
        name = internal.readString(),
        imageUrl = internal.readString(),
        phoneNumber = internal.readString(),
        description = internal.readString(),
        address = internal.readString(),
        location = internal.readString(),
        locationLongitude = internal.readDouble(),
        locationLatitude = internal.readDouble()
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