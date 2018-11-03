package no.aegisdynamics.habitat.dashboard;


import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.weather.Weather;

/**
 * Specifies the contract between the Dashboard view and presenter.
 */

interface DashboardContract {

    interface View {
        void showPlacePicker();
        void showWeatherData(Weather weather);
        void showWeatherDataError(String error);
        void showDevices(List<Device> devices);
        void showDevicesLoadError(String error);
        void setProgressIndicator(boolean active);
        void showDeviceDetailUI(@NonNull Device device);
        void showCommandFailedMessage(String error);
        void showCommandSuccessMessage();
        void showDeviceTokenRegistrationError(String error);
        void showDeviceTokenRegistered();
        void showLockStateWarning(int numberOfLocksOpen);
        void hideLockStateWarning();
        void showAppSupportInfoDialog();
        void onDashboardDevicesRetrieved(List<String> dashboardDevices);
        void onDashboardDevicesRetrieveError(String error);
    }

    interface UserActionsListener {
        void getWeatherData(double lat, double lon);
        void getProfileData(String username);
        void loadDevices();
        void openDeviceDetails(@NonNull Device requestedDevice);
        void sendCommand(@NonNull Device requestedDevice, @NonNull String command);
        void registerFCMDeviceToken(String deviceToken);
    }

    interface Activity {
        void showLocationAddHint();
        void showDevicesHint();
    }
}
