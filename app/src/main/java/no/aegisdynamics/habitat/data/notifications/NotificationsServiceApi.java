package no.aegisdynamics.habitat.data.notifications;

import android.content.Context;

import java.util.List;

/**
 * Defines an interface to the service API that is used by this application. All notification data requests should
 * be piped through this interface.
 */
interface NotificationsServiceApi {

    interface NotificationsServiceCallback<T> {
        void onLoaded (T notifications);
        void onError (String error);
    }

    void getAllNotifications(Context context, NotificationsServiceCallback<List<Notification>> callback);

    void getNotificationsForDevice(Context context, String deviceId, NotificationsServiceCallback<List<Notification>> callback);

    void updateNotification(Context context, long notificationId, NotificationsServiceCallback<Boolean> callback);

    void deleteNotification(Context context, long notificationId, NotificationsServiceCallback<Boolean> callback);
}
