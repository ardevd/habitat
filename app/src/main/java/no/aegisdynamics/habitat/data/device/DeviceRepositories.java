package no.aegisdynamics.habitat.data.device;


import android.content.Context;
import android.support.annotation.NonNull;

public class DeviceRepositories {

    private DeviceRepositories() {
        // No instance
    }

    private static DevicesRepository repository = null;

    public synchronized static DevicesRepository getRepository(@NonNull Context context, @NonNull DevicesServiceApi devicesServiceApi) {
        repository = new APIDeviceProviderRepository(devicesServiceApi, context);
        return repository;
    }

}
