package no.aegisdynamics.habitat.locationdetail;


import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.data.location.LocationsRepository;

/**
 * Listens to user actions from the UI ({@link LocationDetailFragment}), retrieves the data and updates the
 * UI as required.
 */
public class LocationDetailPresenter implements LocationDetailContract.UserActionsListener {


    private final DevicesRepository mDevicesRepository;
    private final LocationsRepository mLocationsRepository;
    private final LocationDetailContract.View mLocationDetailView;


    public LocationDetailPresenter(@NonNull DevicesRepository devicesRepository,
                                 @NonNull LocationsRepository locationsRepository,
                                 @NonNull LocationDetailContract.View locationDetailView) {
        mDevicesRepository = devicesRepository;
        mLocationsRepository = locationsRepository;
        mLocationDetailView = locationDetailView;
    }

    @Override
    public void openLocation(int locationId) {
        if (locationId < 0) {
            mLocationDetailView.showMissingLocation();
            return;
        }

        mLocationDetailView.setProgressIndicator(true);
        mLocationsRepository.getLocation(locationId, new LocationsRepository.GetLocationCallback() {
            @Override
            public void onLocationLoaded(Location location) {
                mLocationDetailView.setProgressIndicator(false);
                if (location == null) {
                    mLocationDetailView.showMissingLocation();
                } else {
                    showLocation(location);
                }
            }

            @Override
            public void onLocationLoadError(String error) {
                mLocationDetailView.setProgressIndicator(false);
                mLocationDetailView.showLocationLoadError(error);
            }
        });

    }

    @Override
    public void openDeviceDetails(@NonNull Device requestedDevice) {
        mLocationDetailView.showDeviceDetailUI(requestedDevice);
    }

    @Override
    public void loadDevicesForLocation(int locationId) {
        mLocationDetailView.setProgressIndicator(true);
        mDevicesRepository.getDevicesForLocation(locationId, new DevicesRepository.GetDevicesForLocationCallback() {
            @Override
            public void onDevicesForLocationLoaded(List<Device> devices) {
                mLocationDetailView.setProgressIndicator(false);
                mLocationDetailView.showDevices(devices);
                mLocationDetailView.showDeviceCount(devices.size());
            }

            @Override
            public void onDevicesForLocationLoadError(String error) {
                mLocationDetailView.setProgressIndicator(false);
                mLocationDetailView.showDevicesLoadError(error);
            }
        });
    }

    @Override
    public void sendCommand(@NonNull Device requestedDevice, @NonNull String command) {
        // Send device command to server.
        mLocationDetailView.setProgressIndicator(true);
        mDevicesRepository.sendCommand(requestedDevice.getId(), command, new DevicesRepository.SendCommandCallback() {


            @Override
            public void onCommandFailed(String error) {
                mLocationDetailView.showCommandFailedMessage(error);
                mLocationDetailView.setProgressIndicator(false);
            }

            @Override
            public void onCommandSent() {
                mLocationDetailView.setProgressIndicator(false);
                mLocationDetailView.showCommandStatusMessage(true);
            }
        });
    }

    @Override
    public void editLocation(int locationId, String name) {
        // Post updated location title to server.
        mLocationDetailView.setProgressIndicator(true);
        mLocationsRepository.updateLocation(locationId, name, new LocationsRepository.UpdateLocationCallback() {

            @Override
            public void onLocationUpdated() {
                mLocationDetailView.setProgressIndicator(false);
                mLocationDetailView.showLocationUpdated();
            }

            @Override
            public void onLocationUpdateFailed(String error) {
                mLocationDetailView.setProgressIndicator(false);
                mLocationDetailView.showLocationUpdateError(error);

            }
        });
    }

    private void showLocation(Location location) {

        String imageName = location.getImageName();
        String locationTitle = location.getTitle();

        if (imageName != null) {
            mLocationDetailView.showImage(imageName);
        } else {
            mLocationDetailView.hideImage();
        }

        if (locationTitle != null) {
            mLocationDetailView.showTitle(locationTitle);
        } else {
            mLocationDetailView.hideTitle();
        }

    }
}
