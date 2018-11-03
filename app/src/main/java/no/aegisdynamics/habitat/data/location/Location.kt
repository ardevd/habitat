package no.aegisdynamics.habitat.data.location

import android.os.Parcel
import android.os.Parcelable

/**
 * Immutable model class for a Z-Wave location
 */
class Location(val id: Int,
               val title: String?,
               val imageName: String?,
               val deviceCount: Int) : Parcelable {

    val isEmpty: Boolean
        get() = title == null || "" == title

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    fun hasImage(): Boolean {
        return imageName != null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(imageName)
        parcel.writeInt(deviceCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }
}

