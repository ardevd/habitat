package no.aegisdynamics.habitat.data.device;


import android.support.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing device data.
 */

public interface DevicesRepository  {

    interface LoadDevicesCallback {

        void onDevicesLoaded(List<Device> devices);

        void onDevicesLoadError(String error);
    }

    interface GetDeviceCallback {

        void onDeviceLoaded(Device device);

        void onDeviceLoadError(String error);
    }

    interface SendCommandCallback {
        void onCommandSent();

        void onCommandFailed(String error);
    }

    interface SendAutomatedCommandCallback {
        void onAutomatedCommandSent(String automationTitle);

        void onAutomatedCommandFailed(String error, String automationTitle);
    }

    interface GetControllerStatusCallback {
        void onControllerUp();

        void onControllerDown(String error);
    }

    interface GetDevicesForLocationCallback {
        void onDevicesForLocationLoaded(List<Device> devices);

        void onDevicesForLocationLoadError(String error);
    }

    interface RegisterFCMDeviceTokenCallback {
        void onTokenRegistered();

        void onTokenRegisterError(String error);
    }

    void getDevices(@NonNull LoadDevicesCallback callback);

    void sendCommand(@NonNull String deviceId, @NonNull String command, @NonNull SendCommandCallback callback);

    void sendAutomatedCommand(@NonNull String deviceId, @NonNull String command, @NonNull String automationTitle,
                              @NonNull SendAutomatedCommandCallback callback);

    void getDevice(@NonNull String deviceId, @NonNull GetDeviceCallback callback);

    void getControllerStatus(@NonNull GetControllerStatusCallback callback);

    void getDevicesForLocation(int locationId, @NonNull GetDevicesForLocationCallback callback);

    void registerFCMDeviceToken(@NonNull String deviceToken, @NonNull RegisterFCMDeviceTokenCallback callback);
}

