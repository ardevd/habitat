package no.aegisdynamics.habitat.notifications;

import java.util.List;

import no.aegisdynamics.habitat.data.notifications.Notification;

public interface NotificationsContract {

    interface View {
        void showNotifications(List<Notification> notifications);
        void showNotificationsLoadError(String error);
        void setProgressIndicator(boolean active);
        void showNotificationDeleted();
        void showNotificationDeletedError(String error);
        void showNotificationRedeemed();
        void showNotificationRedeemedError(String error);
    }
    interface UserActionsListener {
        void loadNotifications();
        void deleteNotification(Notification notification);
        void redeemNotification(Notification notification);
    }
}
