package no.aegisdynamics.habitat.data.device;

import android.content.Context;

import java.util.List;

/**
 * Defines an interface to the service API that is used by this application. All device data requests should
 * be piped through this interface.
 */

public interface DevicesServiceApi {

    interface DevicesServiceCallback<T> {
        void onLoaded (T devices);
        void onError (String error);
    }

    interface AutomatedCommandServiceCallback<T> {
        void onSuccess (String automationTitle);
        void onError (String error, String automationTitle);
    }

    interface FCMDeviceTokenRegistrationServiceCallback<T> {
        void onSuccess ();
        void onError (String error);
    }

    void getAllDevices(Context context, DevicesServiceCallback<List<Device>> callback);

    void getDevice(Context context, String deviceId, DevicesServiceCallback<Device> callback);

    void sendCommand(Context context, String deviceId, String command, DevicesServiceCallback<Boolean> callback);

    void sendAutomatedCommand(Context context, String deviceId, String command, String automationTitle,
                              AutomatedCommandServiceCallback<Boolean> callback);

    void getControllerStatus(Context context, DevicesServiceCallback<Boolean> callback);

    void registerFCMDeviceToken(Context context, String deviceToken, FCMDeviceTokenRegistrationServiceCallback callback);

}

