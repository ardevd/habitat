package no.aegisdynamics.habitat.data.notifications;

import android.content.Context;
import android.support.annotation.NonNull;

public class NotificationRepositories {

    private NotificationRepositories() {
        // No instance
    }

    private static NotificationsRepository repository = null;

    public synchronized static NotificationsRepository getRepository(@NonNull Context context, @NonNull NotificationsServiceApi notificationsServiceApi) {
        repository = new APINotificationProviderRepository(notificationsServiceApi, context);
        return repository;
    }
}
