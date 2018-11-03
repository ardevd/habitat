package no.aegisdynamics.habitat.data.device;

import android.content.Context;
import android.support.annotation.NonNull;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.device.DevicesServiceApi;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation to load devices from ZAutomation API
 */
public class APIDeviceProviderRepository implements DevicesRepository, DeviceDataContract {

    private final DevicesServiceApi mDevicesServiceApi;
    private final Context mContext;

    public APIDeviceProviderRepository(@NonNull DevicesServiceApi devicesServiceApi, @NonNull Context context) {
        mDevicesServiceApi = devicesServiceApi;
        mContext = context;
    }

    @Override
    public void getDevices(@NonNull final LoadDevicesCallback callback) {
        mDevicesServiceApi.getAllDevices(mContext, new DevicesServiceApi.DevicesServiceCallback<List<Device>>() {

            @Override
            public void onLoaded(List<Device> devices) {
                callback.onDevicesLoaded(devices);
            }

            @Override
            public void onError(String error) {
                callback.onDevicesLoadError(error);
            }
        });
    }

    @Override
    public void sendCommand(@NonNull String deviceId, @NonNull String command, @NonNull final SendCommandCallback callback) {
        mDevicesServiceApi.sendCommand(mContext, deviceId, command, new DevicesServiceApi.DevicesServiceCallback<Boolean>() {

            @Override
            public void onLoaded(Boolean success) {
                callback.onCommandSent();
            }

            @Override
            public void onError(String error) {
                callback.onCommandFailed(error);
            }
        });
    }

    @Override
    public void sendAutomatedCommand(@NonNull String deviceId, @NonNull String command, @NonNull String automationTitle, @NonNull final SendAutomatedCommandCallback callback) {
        mDevicesServiceApi.sendAutomatedCommand(mContext, deviceId, command, automationTitle,
                new DevicesServiceApi.AutomatedCommandServiceCallback() {

                    @Override
                    public void onSuccess(String automationTitle) {
                        callback.onAutomatedCommandSent(automationTitle);
                    }

                    @Override
                    public void onError(String error, String automationTitle) {
                        callback.onAutomatedCommandFailed(error, automationTitle);

                    }
                });
    }


    @Override
    public void getDevice(@NonNull String deviceId, @NonNull final GetDeviceCallback callback) {
        mDevicesServiceApi.getDevice(mContext, deviceId, new DevicesServiceApi.DevicesServiceCallback<Device>() {

            @Override
            public void onLoaded(Device devices) {
                callback.onDeviceLoaded(devices);
            }

            @Override
            public void onError(String error) {
                callback.onDeviceLoadError(error);
            }
        });
    }

    @Override
    public void getControllerStatus(final @NonNull GetControllerStatusCallback callback) {
        mDevicesServiceApi.getControllerStatus(mContext, new DevicesServiceApi.DevicesServiceCallback<Boolean>() {

            @Override
            public void onLoaded(Boolean devices) {
                callback.onControllerUp();
            }

            @Override
            public void onError(String error) {
                callback.onControllerDown(error);
            }
        });
    }

    @Override
    public void getDevicesForLocation(final int locationId, final @NonNull GetDevicesForLocationCallback callback) {
        mDevicesServiceApi.getAllDevices(mContext, new DevicesServiceApi.DevicesServiceCallback<List<Device>>() {

            @Override
            public void onLoaded(List<Device> devices) {
                // Remove devices not associated with locationId.
                List<Device> filteredDevices = new ArrayList<>();
                for (Device device : devices) {
                    if (device.getLocationId() == locationId) {
                        filteredDevices.add(device);
                    }
                }
                callback.onDevicesForLocationLoaded(filteredDevices);
            }

            @Override
            public void onError(String error) {
                callback.onDevicesForLocationLoadError(error);
            }
        });
    }

    @Override
    public void registerFCMDeviceToken(@NonNull String deviceToken, @NonNull final RegisterFCMDeviceTokenCallback callback) {
        mDevicesServiceApi.registerFCMDeviceToken(mContext, deviceToken, new DevicesServiceApi.FCMDeviceTokenRegistrationServiceCallback() {
            @Override
            public void onSuccess() {
                callback.onTokenRegistered();
            }

            @Override
            public void onError(String error) {
                callback.onTokenRegisterError(error);
            }
        });
    }

    @Override
    public void getControllerData(@NonNull final GetControllerDataCallback callback) {
        mDevicesServiceApi.getControllerData(mContext, new DevicesServiceApi.DevicesServiceCallback<Controller>() {
            @Override
            public void onLoaded(Controller controller) {
                callback.onControllerDataLoaded(controller);
            }

            @Override
            public void onError(String error) {
                callback.onControllerDataLoadError(error);
            }
        });
    }

}
