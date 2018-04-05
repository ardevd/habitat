package no.aegisdynamics.habitat.notifications;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.notifications.Notification;
import no.aegisdynamics.habitat.data.notifications.NotificationsRepository;

/**
 * Listens to user actions from the UI ({@link NotificationsFragment}), retrieves the data and updates the
 * UI as required.
 */
public class NotificationsPresenter implements NotificationsContract.UserActionsListener {

    private final NotificationsRepository mNotificationsRepository;
    private final NotificationsContract.View mNotificationsView;

    public NotificationsPresenter(@NonNull NotificationsRepository notificationsRepository, @NonNull NotificationsContract.View notificationsView){
        mNotificationsRepository = notificationsRepository;
        mNotificationsView = notificationsView;
    }

    @Override
    public void loadNotifications() {
        // Get list of notifications from API server.
        mNotificationsView.setProgressIndicator(true);
        mNotificationsRepository.getNotifications(new NotificationsRepository.LoadNotificationsCallback() {

            @Override
            public void onNotificationsLoaded(List<Notification> notifications) {
                mNotificationsView.setProgressIndicator(false);
                mNotificationsView.showNotifications(notifications);
            }

            @Override
            public void onNotificationsLoadError(String error) {
                mNotificationsView.setProgressIndicator(false);
                mNotificationsView.showNotificationsLoadError(error);
            }
        });
    }

    @Override
    public void deleteNotification(Notification notification) {
        mNotificationsRepository.deleteNotification(notification.getId(), new NotificationsRepository.DeleteNotificationCallback() {

            @Override
            public void onNotificationDeleted() {
                mNotificationsView.showNotificationDeleted();
            }

            @Override
            public void onNotificationDeleteError(String error) {
                mNotificationsView.showNotificationDeletedError(error);
            }
        });
    }

    @Override
    public void redeemNotification(Notification notification) {
        mNotificationsRepository.updateNotification(notification.getId(), new NotificationsRepository.UpdateNotificationCallback() {
            @Override
            public void onNotificationUpdated() {
                mNotificationsView.showNotificationRedeemed();
            }

            @Override
            public void onNotificationUpdateError(String error) {
                mNotificationsView.showNotificationRedeemedError(error);
            }
        });
    }
}
