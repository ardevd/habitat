package no.aegisdynamics.habitat.data.device

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
             val iconName: String?) : DeviceDataContract {

    val id: String
        get() = deviceId

    val isSwitchable: Boolean
        get() = type == DeviceDataContract.DEVICE_TYPE_SWITCH_BINARY || type == DeviceDataContract.DEVICE_TYPE_DOOR_LOCK

    val isEmpty: Boolean
        get() = title == null || "" == title

    fun hasIcon(): Boolean {
        return iconName != null
    }

    override fun toString(): String {
        return title
    }
}
