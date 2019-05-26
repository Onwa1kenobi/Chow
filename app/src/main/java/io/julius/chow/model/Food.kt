package io.julius.chow.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Food(
    val id: String,
    val title: String,
    val imageUrl: String,
    val description: String,
    val restaurantId: String,
    val price: Double,
    val rating: Double
) : Parcelable {

    var displayPrice = String.format(Locale.getDefault(), "%s %.2f", "\u20A6", price)

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeString(id)
            it.writeString(title)
            it.writeString(imageUrl)
            it.writeString(description)
            it.writeString(restaurantId)
            it.writeDouble(price)
            it.writeDouble(rating)
        }
    }

    constructor(internal: Parcel) : this(
        id = internal.readString(),
        title = internal.readString(),
        imageUrl = internal.readString(),
        description = internal.readString(),
        restaurantId = internal.readString(),
        rating = internal.readDouble(),
        price = internal.readDouble()
    )

    companion object CREATOR : Parcelable.Creator<Food> {
        override fun createFromParcel(parcel: Parcel): Food {
            return Food(parcel)
        }

        override fun newArray(size: Int): Array<Food?> {
            return arrayOfNulls(size)
        }
    }
}