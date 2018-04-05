package no.aegisdynamics.habitat.data.automation

import no.aegisdynamics.habitat.provider.DeviceDataContract

/**
 * Immutable model class for a Habitat automation
 */

class Automation(var id: Int,
                 val name: String?,
                 val description: String,
                 val type: String,
                 val trigger: String?,
                 val commands: String?,
                 val deviceId: String?) : DeviceDataContract {

    val isEmpty: Boolean
        get() = (name == null || "" == name
                || deviceId == null || "" == deviceId
                || commands == null || "" == commands
                || trigger == null || "" == trigger)

}