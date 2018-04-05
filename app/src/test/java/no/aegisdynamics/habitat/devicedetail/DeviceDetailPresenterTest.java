package no.aegisdynamics.habitat.devicedetail;


import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.notifications.Notification;
import no.aegisdynamics.habitat.data.notifications.NotificationsRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class DeviceDetailPresenterTest {

    // Dummy Device for testing
    private static Device DEVICE = new Device("test-device-01", "Test Device", "switchBinary", "Office", 1, 1, null, "on", 0, 0, null, null, "icon");

    // Dummy notifications
    private static List<Notification> NOTIFICATIONS = Lists.newArrayList(new Notification(12345, new Date(), "device_id", "device name", "message", false),
            new Notification(34512512, new Date(), "device_id_2", "second device name", "important message", true));

    @Mock
    private DevicesRepository mDevicesRepository;

    @Mock
    private DeviceDetailContract.View mDeviceDetailView;

    @Mock
    private NotificationsRepository mNotificationsRepository;

    @Captor
    private ArgumentCaptor<DevicesRepository.GetDeviceCallback> mGetDeviceCallbackCaptor;

    @Captor
    private ArgumentCaptor<NotificationsRepository.GetNotificationsForDeviceCallback> mGetNotificationsForDeviceCallbackCaptor;

    @Captor
    private ArgumentCaptor<DevicesRepository.SendCommandCallback> mSendCommandCallbackCaptor;

    @Captor
    private ArgumentCaptor<NotificationsRepository.DeleteNotificationCallback> mDeleteNotificationsCallbackCaptor;

    @Captor
    private ArgumentCaptor<NotificationsRepository.UpdateNotificationCallback> mUpdateNotificationsCallbackCaptor;


    private DeviceDetailPresenter mDeviceDetailPresenter;

    @Before
    public void setupDevicePresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mDeviceDetailPresenter = new DeviceDetailPresenter(mDevicesRepository, mNotificationsRepository, mDeviceDetailView);
    }

    @Test
    public void getDeviceFromRepositoryAndLoadIntoView() {
        mDeviceDetailPresenter.openDevice(String.valueOf(DEVICE.getId()));

        // Then device is loaded from model, callback is captured and progress indicator is shown
        verify(mDevicesRepository).getDevice(eq(DEVICE.getId()), mGetDeviceCallbackCaptor.capture());
        verify(mDeviceDetailView).setProgressIndicator(true);
        // When device is finally loaded
        mGetDeviceCallbackCaptor.getValue().onDeviceLoaded(DEVICE); // Trigger callback

        // Then progress indicator is hidden and title, destination, description, start/end time is shown in UI.
        verify(mDeviceDetailView).setProgressIndicator(false);
        verify(mDeviceDetailView).showTitle(DEVICE.getTitle());
        verify(mDeviceDetailView).showLocation(DEVICE.getLocation());
    }

    @Test
    public void getDeviceFromRepositoryFailureAndShowError() {
        mDeviceDetailPresenter.openDevice(String.valueOf(DEVICE.getId()));

        // Then device is loaded from model, callback is captured and progress indicator is shown
        verify(mDevicesRepository).getDevice(eq(DEVICE.getId()), mGetDeviceCallbackCaptor.capture());
        verify(mDeviceDetailView).setProgressIndicator(true);
        // When device fails to load the device
        mGetDeviceCallbackCaptor.getValue().onDeviceLoadError("Device load error test"); // Trigger callback

        // Then progress indicator is hidden and error message is shown in UI
        verify(mDeviceDetailView).setProgressIndicator(false);
        verify(mDeviceDetailView).showDeviceLoadError(eq("Device load error test"));
    }

    @Test
    public void sendCommandToDeviceFromDeviceDetail() {
        // When sending a command to a device
        mDeviceDetailPresenter.sendCommand(DEVICE, "on");

        // Progress indicator is shown
        verify(mDeviceDetailView).setProgressIndicator(true);

        // Command is sent
        verify(mDevicesRepository).sendCommand(eq(DEVICE.getId()), eq("on"), mSendCommandCallbackCaptor.capture());
        mSendCommandCallbackCaptor.getValue().onCommandSent();

        // Progress indicator is hidden and message is shown in the UI
        verify(mDeviceDetailView).setProgressIndicator(false);
        verify(mDeviceDetailView).showCommandStatusMessage(eq(true));
    }

    @Test
    public void sendCommandFailedToDeviceFromDeviceDetail() {
        // When sending a command to a device
        mDeviceDetailPresenter.sendCommand(DEVICE, "on");

        // Progress indicator is shown
        verify(mDeviceDetailView).setProgressIndicator(true);

        // Command is sent
        verify(mDevicesRepository).sendCommand(eq(DEVICE.getId()), eq("on"), mSendCommandCallbackCaptor.capture());
        mSendCommandCallbackCaptor.getValue().onCommandFailed("Error");

        // Progress indicator is hidden and message is shown in the UI
        verify(mDeviceDetailView).setProgressIndicator(false);
        verify(mDeviceDetailView).showCommandFailedMessage(eq("Error"));
    }


    @Test
    public void getNotificationsFromRepositoryAndLoadIntoView() {
        mDeviceDetailPresenter.loadNotificationsForDevice(DEVICE.getId());

        verify(mNotificationsRepository).getNotificationsForDevice(eq(DEVICE.getId()), mGetNotificationsForDeviceCallbackCaptor.capture());
        verify(mDeviceDetailView).setProgressIndicator(true);

        mGetNotificationsForDeviceCallbackCaptor.getValue().onNotificationsForDeviceLoaded(NOTIFICATIONS);


        // Progress indicator is hidden and notifications are shown in the UI
        verify(mDeviceDetailView).setProgressIndicator(false);
        verify(mDeviceDetailView).showNotifications(NOTIFICATIONS);
    }

    @Test
    public void deleteNotification() {
        mDeviceDetailPresenter.deleteNotification(NOTIFICATIONS.get(1));

        verify(mNotificationsRepository).deleteNotification(eq(NOTIFICATIONS.get(1).getId()), mDeleteNotificationsCallbackCaptor.capture());
        mDeleteNotificationsCallbackCaptor.getValue().onNotificationDeleted();

        // Message is shown in the UI
        verify(mDeviceDetailView).showNotificationDeleted();
    }

    @Test
    public void redeemNotification() {
        mDeviceDetailPresenter.redeemNotification(NOTIFICATIONS.get(1));

        verify(mNotificationsRepository).updateNotification(eq(NOTIFICATIONS.get(1).getId()), mUpdateNotificationsCallbackCaptor.capture());
        mUpdateNotificationsCallbackCaptor.getValue().onNotificationUpdated();

        //Message is shown in the UI
        verify(mDeviceDetailView).showNotificationRedeemed();
    }

}