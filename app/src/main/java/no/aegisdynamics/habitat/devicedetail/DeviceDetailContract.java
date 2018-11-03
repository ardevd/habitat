package no.aegisdynamics.habitat.devicedetail;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.notifications.Notification;


interface DeviceDetailContract {
    
        interface View {
            void setProgressIndicator(boolean active);
            void showMissingDevice();
            void showDeviceLoadError(String error);
            void showTitle(String title);
            void showLocation(String location);
            void hideLocation();
            void showState(Device device);
            void hideState();
            void showTags(String[] tags);
            void hideTags();
            void showDeviceControls(String deviceType);
            void showCommandStatusMessage(boolean success);
            void showCommandFailedMessage(String error);
            void updateFavoriteIndicator(String deviceId);
            void showNotifications(List<Notification> notifications);
            void showNotificationsLoadError(String error);
            void showNotificationRedeemed();
            void showNotificationDeleted();
            void showNotificationUpdateError(String error);
            void showCustomCommandDialog();

        }
        interface UserActionsListener {
            void loadNotificationsForDevice(@NonNull String deviceId);
            void openDevice(@NonNull String deviceId);
            void sendCommand(@NonNull Device requestedDevice, @NonNull String command);
            void makeFavorite(@NonNull String deviceId, @NonNull Context context);
            void makeNotFavorite(@NonNull String deviceId, @NonNull Context context);
            void redeemNotification(Notification notification);
            void deleteNotification(Notification notification);
        }

}
