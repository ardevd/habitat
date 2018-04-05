package no.aegisdynamics.habitat.data.notifications;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Concrete implementation to load notifications from ZAutomation API
 */
public class APINotificationProviderRepository implements NotificationsRepository {

    private final NotificationsServiceApi mNotificationsServiceApi;
    private final Context mContext;

    public APINotificationProviderRepository(@NonNull NotificationsServiceApi notificationsServiceApi, @NonNull Context context) {
        mNotificationsServiceApi = notificationsServiceApi;
        mContext = context;
    }
    
    
    @Override
    public void getNotifications(@NonNull final LoadNotificationsCallback callback) {
        mNotificationsServiceApi.getAllNotifications(mContext, new NotificationsServiceApi.NotificationsServiceCallback<List<Notification>>() {

            @Override
            public void onLoaded(List<Notification> notifications) {
                callback.onNotificationsLoaded(notifications);
            }

            @Override
            public void onError(String error) {
                callback.onNotificationsLoadError(error);
            }
        });
    }

    @Override
    public void getNotificationsForDevice(@NonNull String deviceId, @NonNull final GetNotificationsForDeviceCallback callback) {
        mNotificationsServiceApi.getNotificationsForDevice(mContext, deviceId, new NotificationsServiceApi.NotificationsServiceCallback<List<Notification>>() {

            @Override
            public void onLoaded(List<Notification> notifications) {
                callback.onNotificationsForDeviceLoaded(notifications);
            }

            @Override
            public void onError(String error) {
                callback.onNotificationsForDeviceLoadError(error);
            }
        });

    }

    @Override
    public void updateNotification(long id, @NonNull final UpdateNotificationCallback callback) {
        mNotificationsServiceApi.updateNotification(mContext, id, new NotificationsServiceApi.NotificationsServiceCallback<Boolean>() {
            @Override
            public void onLoaded(Boolean notifications) {
                callback.onNotificationUpdated();
            }

            @Override
            public void onError(String error) {
                callback.onNotificationUpdateError(error);
            }
        });
    }

    @Override
    public void deleteNotification(long id, @NonNull final DeleteNotificationCallback callback) {
        mNotificationsServiceApi.deleteNotification(mContext, id, new NotificationsServiceApi.NotificationsServiceCallback<Boolean>() {
            @Override
            public void onLoaded(Boolean notifications) {
                callback.onNotificationDeleted();
            }

            @Override
            public void onError(String error) {
                callback.onNotificationDeleteError(error);
            }
        });
    }


}
