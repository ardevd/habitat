package no.aegisdynamics.habitat.data.notifications;

import android.content.Context;
import android.support.annotation.NonNull;

public class NotificationRepositories {

    private NotificationRepositories() {
        // No instance
    }


    public static synchronized NotificationsRepository getRepository(@NonNull Context context, @NonNull NotificationsServiceApi notificationsServiceApi) {
        return new APINotificationProviderRepository(notificationsServiceApi, context);
    }
}
