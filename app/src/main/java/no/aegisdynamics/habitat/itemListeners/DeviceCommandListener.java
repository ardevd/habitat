package no.aegisdynamics.habitat.itemListeners;

import no.aegisdynamics.habitat.data.device.Device;


public interface DeviceCommandListener {
    void onCommand(Device commandedDevice, String command);
}
