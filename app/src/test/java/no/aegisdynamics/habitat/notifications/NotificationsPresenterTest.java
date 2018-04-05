package no.aegisdynamics.habitat.notifications;


import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import no.aegisdynamics.habitat.data.notifications.Notification;
import no.aegisdynamics.habitat.data.notifications.NotificationsRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class NotificationsPresenterTest {

    // List with dummy devices for testing
    private static List<Notification> NOTIFICATIONS = Lists.newArrayList(new Notification(1, new Date(), "test device 1", "Device name",
    "Notification message", false), new Notification(51251212, new Date(), "test device 2", "Device name",
            "Notification message", false));

    @Mock
    private NotificationsRepository mNotificationsRepository;

    @Mock
    private NotificationsContract.View mNotificationsView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<NotificationsRepository.LoadNotificationsCallback> mLoadNotificationsCallbackCaptor;

    @Captor
    private ArgumentCaptor<NotificationsRepository.DeleteNotificationCallback> mDeleteNotificationsCallbackCaptor;

    @Captor
    private ArgumentCaptor<NotificationsRepository.UpdateNotificationCallback> mUpdateNotificationsCallbackCaptor;

    private NotificationsPresenter mNotificationsPresenter;

    @Before
    public void setupNotificationPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mNotificationsPresenter = new NotificationsPresenter(mNotificationsRepository, mNotificationsView);
    }

    @Test
    public void loadNotificationsFromRepositoryAndLoadIntoView() {
        // Given an initialized NotificationsPresenter with initialized devices
        // When loading of devices is requested
        mNotificationsPresenter.loadNotifications();

        // Callback is captured and invoked with stubbed notifications
        verify(mNotificationsView).setProgressIndicator(true);
        verify(mNotificationsRepository).getNotifications(mLoadNotificationsCallbackCaptor.capture());
        mLoadNotificationsCallbackCaptor.getValue().onNotificationsLoaded(NOTIFICATIONS);

        // Progress indicator is hidden and notifications are shown in the UI
        verify(mNotificationsView).setProgressIndicator(false);
        verify(mNotificationsView).showNotifications(NOTIFICATIONS);
    }

    @Test
    public void loadNotificationsFromRepositoryFailureAndErrorMessageShown() {
        // Given an initialized NotificationsPresenter with initialized devices
        // When loading of devices is requested
        mNotificationsPresenter.loadNotifications();

        // Callback is captured and invoked with stubbed notifications
        verify(mNotificationsView).setProgressIndicator(true);
        verify(mNotificationsRepository).getNotifications(mLoadNotificationsCallbackCaptor.capture());
        mLoadNotificationsCallbackCaptor.getValue().onNotificationsLoadError("Notification load error!");

        // Progress indicator is hidden and error message is shown in the UI
        verify(mNotificationsView).setProgressIndicator(false);
        verify(mNotificationsView).showNotificationsLoadError("Notification load error!");
    }

    @Test
    public void deleteNotification() {
        mNotificationsPresenter.deleteNotification(NOTIFICATIONS.get(1));

        verify(mNotificationsRepository).deleteNotification(eq(NOTIFICATIONS.get(1).getId()), mDeleteNotificationsCallbackCaptor.capture());
        mDeleteNotificationsCallbackCaptor.getValue().onNotificationDeleted();

        // Message is shown in the UI
        verify(mNotificationsView).showNotificationDeleted();
    }

    @Test
    public void deleteNotificationError() {
        mNotificationsPresenter.deleteNotification(NOTIFICATIONS.get(1));

        verify(mNotificationsRepository).deleteNotification(eq(NOTIFICATIONS.get(1).getId()), mDeleteNotificationsCallbackCaptor.capture());
        mDeleteNotificationsCallbackCaptor.getValue().onNotificationDeleteError("Error");

        // Error message is shown in the UI
        verify(mNotificationsView).showNotificationDeletedError("Error");
    }

    @Test
    public void redeemNotification() {
        mNotificationsPresenter.redeemNotification(NOTIFICATIONS.get(1));

        verify(mNotificationsRepository).updateNotification(eq(NOTIFICATIONS.get(1).getId()), mUpdateNotificationsCallbackCaptor.capture());
        mUpdateNotificationsCallbackCaptor.getValue().onNotificationUpdated();

        //Message is shown in the UI
        verify(mNotificationsView).showNotificationRedeemed();
    }

    @Test
    public void redeemNotificationError() {
        mNotificationsPresenter.redeemNotification(NOTIFICATIONS.get(1));

        verify(mNotificationsRepository).updateNotification(eq(NOTIFICATIONS.get(1).getId()), mUpdateNotificationsCallbackCaptor.capture());
        mUpdateNotificationsCallbackCaptor.getValue().onNotificationUpdateError("Error");

        // Error message is shown in the UI
        verify(mNotificationsView).showNotificationRedeemedError("Error");
    }
}
