package no.aegisdynamics.habitat.locationdetail;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;


public interface LocationDetailContract {

    interface Activity {
        void updateToolbarColors(Bitmap bitmap);
    }

    interface View {

        void setProgressIndicator(boolean active);
        void showLocationLoadError(String error);
        void showMissingLocation();
        void hideTitle();
        void showTitle(String title);
        void showImage(String imageUrl);
        void hideImage();
        void hideDeviceCount();
        void showDeviceCount(String description);
        void showDevices(List<Device> devices);
        void showDevicesLoadError(String error);
        void showCommandFailedMessage(String error);
        void showCommandStatusMessage(boolean success);
        void showDeviceDetailUI(@NonNull Device device);
        void showEditLocationDialog();
        void showLocationUpdated();
        void showLocationUpdateError(String error);

        }

        interface UserActionsListener {
            void openLocation(int locationId);
            void openDeviceDetails(@NonNull Device requestedDevice);
            void loadDevicesForLocation(int locationId);
            void sendCommand(@NonNull Device requestedDevice, @NonNull String command);
            void editLocation(int locationId, String name);
        }
}
