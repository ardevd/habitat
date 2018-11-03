package no.aegisdynamics.habitat.data.device

import android.os.Parcel
import android.os.Parcelable
import no.aegisdynamics.habitat.provider.DeviceDataContract

/**
 * Immutable model class for a Z-Wave device
 */

class Device(private val deviceId: String,
             val title: String,
             val type: String?,
             val location: String?,
             val locationId: Int,
             val creatorId: Int,
             val tags: Array<String>?,
             val status: String?,
             val deviceMinValue: Int,
             val deviceMaxValue: Int,
             val statusNotation: String?,
             val deviceProbeTitle: String?,
             val iconName: String?) : DeviceDataContract, Parcelable {

    val id: String
        get() = deviceId

    val isSwitchable: Boolean
        get() = type == DeviceDataContract.DEVICE_TYPE_SWITCH_BINARY
                || type == DeviceDataContract.DEVICE_TYPE_DOOR_LOCK
                || type == DeviceDataContract.DEVICE_TYPE_TOGGLE_BUTTON

    val isEmpty: Boolean
        get() = title == null || "" == title

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.createStringArray(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    fun hasIcon(): Boolean {
        return iconName != null
    }

    override fun toString(): String {
        return title
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceId)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeString(location)
        parcel.writeInt(locationId)
        parcel.writeInt(creatorId)
        parcel.writeStringArray(tags)
        parcel.writeString(status)
        parcel.writeInt(deviceMinValue)
        parcel.writeInt(deviceMaxValue)
        parcel.writeString(statusNotation)
        parcel.writeString(deviceProbeTitle)
        parcel.writeString(iconName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Device> {
        override fun createFromParcel(parcel: Parcel): Device {
            return Device(parcel)
        }

        override fun newArray(size: Int): Array<Device?> {
            return arrayOfNulls(size)
        }
    }
}
