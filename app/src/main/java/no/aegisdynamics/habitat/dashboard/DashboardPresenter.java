package no.aegisdynamics.habitat.dashboard;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.profile.Profile;
import no.aegisdynamics.habitat.data.profile.ProfileRepository;
import no.aegisdynamics.habitat.data.weather.Weather;
import no.aegisdynamics.habitat.data.weather.WeatherRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Listens to user actions from the UI ({@link DashboardFragment}), retrieves the data and updates the
 * UI as required.
 */
public class DashboardPresenter implements DashboardContract.UserActionsListener, DeviceDataContract {

    private final DashboardContract.View mDashboardView;
    private final WeatherRepository mWeatherRepository;
    private final DevicesRepository mDevicesRepository;
    private final ProfileRepository mProfileRepository;

    public DashboardPresenter(@NonNull DashboardContract.View dashboardView,
                              @NonNull WeatherRepository weatherRepository,
                              @NonNull DevicesRepository devicesRepository,
                              @NonNull ProfileRepository profileRepository) {
        mDashboardView = dashboardView;
        mWeatherRepository = weatherRepository;
        mDevicesRepository = devicesRepository;
        mProfileRepository = profileRepository;
    }

    @Override
    public void getWeatherData(double lat, double lon) {
        mWeatherRepository.getWeather(lat, lon, new WeatherRepository.GetWeatherCallback() {

            @Override
            public void onWeatherLoaded(Weather weather) {
                mDashboardView.showWeatherData(weather);
            }

            @Override
            public void onWeatherLoadError(String error) {
                mDashboardView.showWeatherDataError(error);
            }
        });
    }

    @Override
    public void getProfileData(String username) {
        mDashboardView.setProgressIndicator(true);
        mProfileRepository.getProfile(username, new ProfileRepository.GetProfileCallback() {

            @Override
            public void onProfileLoaded(Profile profile) {
                mDashboardView.onDashboardDevicesRetrieved(profile.getDashboardDevices());
            }

            @Override
            public void onProfileLoadError(String error) {

            }
        });

    }

    @Override
    public void loadDevices() {
        // Get list of devices from API server.
        mDashboardView.setProgressIndicator(true);
        mDevicesRepository.getDevices(new DevicesRepository.LoadDevicesCallback() {

            @Override
            public void onDevicesLoaded(List<Device> devices) {
                mDashboardView.setProgressIndicator(false);
                mDashboardView.showDevices(devices);
                // Iterate devices and check for relevant state warnings
                // Open Locks
                int openLocks = 0;
                for (Device device : devices) {
                    if (device.getType().equals(DEVICE_TYPE_DOOR_LOCK)) {
                        if (device.getStatus().equals("open")) {
                            openLocks++;
                        }
                    }
                }
                if (openLocks > 0) {
                    mDashboardView.showLockStateWarning(openLocks);
                } else{
                    mDashboardView.hideLockStateWarning();
                }
            }

            @Override
            public void onDevicesLoadError(String error) {
                mDashboardView.setProgressIndicator(false);
                mDashboardView.showDevicesLoadError(error);
            }
        });
    }

    @Override
    public void openDeviceDetails(@NonNull Device requestedDevice) {
        mDashboardView.showDeviceDetailUI(requestedDevice);
    }

    @Override
    public void sendCommand(@NonNull Device requestedDevice, @NonNull String command) {

        // Send device command to server.
        mDashboardView.setProgressIndicator(true);
        mDevicesRepository.sendCommand(requestedDevice.getId(), command, new DevicesRepository.SendCommandCallback() {


            @Override
            public void onCommandFailed(String error) {
                mDashboardView.showCommandFailedMessage(error);
                mDashboardView.setProgressIndicator(false);
            }

            @Override
            public void onCommandSent() {
                mDashboardView.setProgressIndicator(false);
                mDashboardView.showCommandSuccessMessage();
            }
        });
    }

    @Override
    public void registerFCMDeviceToken(String deviceToken) {
        mDevicesRepository.registerFCMDeviceToken(deviceToken, new DevicesRepository.RegisterFCMDeviceTokenCallback() {

            @Override
            public void onTokenRegistered() {
                mDashboardView.showDeviceTokenRegistered();
            }

            @Override
            public void onTokenRegisterError(String error) {
                mDashboardView.showDeviceTokenRegistrationError(error);
            }
        });
    }

}
