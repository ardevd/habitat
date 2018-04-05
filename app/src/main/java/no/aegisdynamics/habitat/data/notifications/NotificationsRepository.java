package no.aegisdynamics.habitat.data.notifications;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing notification data.
 */
public interface NotificationsRepository {
    interface LoadNotificationsCallback {

        void onNotificationsLoaded(List<Notification> notifications);

        void onNotificationsLoadError(String error);
    }

    interface GetNotificationsForDeviceCallback {

        void onNotificationsForDeviceLoaded(List<Notification> notifications);

        void onNotificationsForDeviceLoadError(String error);
    }

    interface UpdateNotificationCallback {

        void onNotificationUpdated();

        void onNotificationUpdateError(String error);
    }

    interface DeleteNotificationCallback {

        void onNotificationDeleted();

        void onNotificationDeleteError(String error);
    }



    void getNotifications(@NonNull LoadNotificationsCallback callback);

    void getNotificationsForDevice(@NonNull String deviceId, @NonNull GetNotificationsForDeviceCallback callback);

    void updateNotification(long id, @NonNull UpdateNotificationCallback callback);

    void deleteNotification(long id, @NonNull DeleteNotificationCallback callback);
}
