package no.aegisdynamics.habitat.devices;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;

/**
 * Specifies the contract between the view and the presenter.
 */
public interface DevicesContract {

    interface View {
        void showDevices(List<Device> devices);
        void showDevicesLoadError(String error);
        void setProgressIndicator(boolean active);
        void showCommandStatusMessage(boolean success);
        void showCommandFailedMessage(String error);
        void showDeviceDetailUI(@NonNull Device device);
    }
    interface UserActionsListener {
        void loadDevices();
        void openDeviceDetails(@NonNull Device requestedDevice);
        void sendCommand(@NonNull Device requestedDevice, @NonNull String command);
    }
}
