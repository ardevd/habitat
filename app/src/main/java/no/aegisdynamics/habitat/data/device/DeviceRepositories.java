package no.aegisdynamics.habitat.data.device;


import android.content.Context;
import android.support.annotation.NonNull;

public class DeviceRepositories {

    private DeviceRepositories() {
        // No instance
    }

    public static synchronized DevicesRepository getRepository(@NonNull Context context, @NonNull DevicesServiceApi devicesServiceApi) {
        return new APIDeviceProviderRepository(devicesServiceApi, context);
    }

}
