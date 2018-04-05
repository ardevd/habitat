package no.aegisdynamics.habitat.itemListeners;


import no.aegisdynamics.habitat.data.notifications.Notification;

public interface NotificationItemListener {

    void onNotificationClick(Notification clickedNotification);

    void onNotificationDeleteClick(Notification clickedNotification);

    void onNotificationRedeemClick(Notification clickedNotification);

}
