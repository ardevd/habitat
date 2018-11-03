package no.aegisdynamics.habitat.devicedetail;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.notifications.Notification;
import no.aegisdynamics.habitat.data.notifications.NotificationsRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;
import no.aegisdynamics.habitat.util.FavoritesHelper;

/**
 * Listens to user actions from the UI ({@link DeviceDetailFragment}), retrieves the data and updates the
 * UI as required.
 */
public class DeviceDetailPresenter implements DeviceDetailContract.UserActionsListener, DeviceDataContract {

    private final DevicesRepository mDevicesRepository;
    private final NotificationsRepository mNotificationsRepository;
    private final DeviceDetailContract.View mDeviceDetailView;

    DeviceDetailPresenter(@NonNull DevicesRepository devicesRepository,
                          @NonNull NotificationsRepository notificationRepository,
                          @NonNull DeviceDetailContract.View devicesView) {
        mDevicesRepository = devicesRepository;
        mNotificationsRepository = notificationRepository;
        mDeviceDetailView = devicesView;
    }

    @Override
    public void loadNotificationsForDevice(@NonNull String deviceId) {
        // Get list of notifications from API server.
        mDeviceDetailView.setProgressIndicator(true);
        mNotificationsRepository.getNotificationsForDevice(deviceId, new NotificationsRepository.GetNotificationsForDeviceCallback() {

            @Override
            public void onNotificationsForDeviceLoaded(List<Notification> notifications) {
                mDeviceDetailView.setProgressIndicator(false);
                mDeviceDetailView.showNotifications(notifications);
            }

            @Override
            public void onNotificationsForDeviceLoadError(String error) {
                mDeviceDetailView.setProgressIndicator(false);
                mDeviceDetailView.showNotificationsLoadError(error);
            }

        });
    }

    @Override
    public void openDevice(@NonNull String deviceId) {
        if (deviceId.isEmpty()) {
            mDeviceDetailView.showMissingDevice();
            return;
        }
        mDeviceDetailView.setProgressIndicator(true);
        mDevicesRepository.getDevice(deviceId, new DevicesRepository.GetDeviceCallback() {
            @Override
            public void onDeviceLoaded(Device device) {
                mDeviceDetailView.setProgressIndicator(false);
                if (device == null) {
                    mDeviceDetailView.showMissingDevice();
                } else {
                    try {
                        showDevice(device);
                        mDeviceDetailView.updateFavoriteIndicator(device.getId());
                    } catch (NullPointerException ex) {
                        // Activity no longer exists. User probably closed it before data returned
                        // No action necessary.
                    }
                }
            }

            @Override
            public void onDeviceLoadError(String error) {
                mDeviceDetailView.setProgressIndicator(false);
                mDeviceDetailView.showDeviceLoadError(error);
            }
        });
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull String command) {
        // Send device command to server.
        mDeviceDetailView.setProgressIndicator(true);
        mDevicesRepository.sendCommand(device.getId(), command, new DevicesRepository.SendCommandCallback() {


            @Override
            public void onCommandFailed(String error) {
                mDeviceDetailView.showCommandFailedMessage(error);
                mDeviceDetailView.setProgressIndicator(false);
            }

            @Override
            public void onCommandSent() {
                mDeviceDetailView.setProgressIndicator(false);
                mDeviceDetailView.showCommandStatusMessage(true);
            }
        });

    }

    @Override
    public void makeFavorite(@NonNull String deviceId, @NonNull Context context) {
        FavoritesHelper.addDeviceIdToFavorites(context, deviceId);
        mDeviceDetailView.updateFavoriteIndicator(deviceId);
    }

    @Override
    public void makeNotFavorite(@NonNull String deviceId, @NonNull Context context) {
        FavoritesHelper.removeDeviceIdFromFavorites(context, deviceId);
        mDeviceDetailView.updateFavoriteIndicator(deviceId);
    }

    @Override
    public void redeemNotification(Notification notification) {
        mNotificationsRepository.updateNotification(notification.getId(), new NotificationsRepository.UpdateNotificationCallback() {
            @Override
            public void onNotificationUpdated() {
                mDeviceDetailView.showNotificationRedeemed();
            }

            @Override
            public void onNotificationUpdateError(String error) {
                mDeviceDetailView.showNotificationUpdateError(error);
            }
        });
    }

    @Override
    public void deleteNotification(Notification notification) {
        mNotificationsRepository.deleteNotification(notification.getId(), new NotificationsRepository.DeleteNotificationCallback() {

            @Override
            public void onNotificationDeleted() {
                mDeviceDetailView.showNotificationDeleted();
            }

            @Override
            public void onNotificationDeleteError(String error) {
                mDeviceDetailView.showNotificationUpdateError(error);
            }
        });
    }


    private void showDevice(Device device) {
        String title = device.getTitle();
        String location = device.getLocation();
        String status = device.getStatus();
        String[] tags = device.getTags();
        String type = device.getType();

        mDeviceDetailView.showTitle(title);

        if (location != null) {
            mDeviceDetailView.showLocation(location);
        } else {
            mDeviceDetailView.hideLocation();
        }

        if (status != null) {
            mDeviceDetailView.showState(device);
        } else {
            mDeviceDetailView.hideState();
        }

        if (tags != null && tags.length > 0) {
            mDeviceDetailView.showTags(tags);
        } else {
            mDeviceDetailView.hideTags();
        }

        if (type != null) {
            mDeviceDetailView.showDeviceControls(type);
        }
    }
}
