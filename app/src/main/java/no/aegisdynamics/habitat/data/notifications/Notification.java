package no.aegisdynamics.habitat.data.notifications;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.Objects;

/**
 * Immutable model class for a Z-Wave notification class
 */
public class Notification implements Comparable<Notification> {

    private final long notificationId;
    @Nullable
    private final Date notificationTimestamp;
    @NonNull
    private final String notificationDeviceId;
    @Nullable
    private final String notificationDeviceName;
    @NonNull
    private final String notificationMessage;

    private boolean notificationRedeemed;

    public Notification(long id,
                        @Nullable Date timestamp,
                        @Nullable String deviceId,
                        @Nullable String deviceName,
                        @Nullable String message,
                        boolean redeemed) {

        notificationId = id;
        notificationTimestamp = timestamp;
        notificationDeviceId = deviceId;
        notificationDeviceName = deviceName;
        notificationMessage = message;
        notificationRedeemed = redeemed;
    }

    public long getId() {
        return notificationId;
    }

    public Date getTimestamp() {
        return notificationTimestamp;
    }

    public String getDeviceId() {
        return notificationDeviceId;
    }

    public String getDeviceName() {
        return notificationDeviceName;
    }

    public String getMessage() {
        return notificationMessage;
    }

    public boolean isRedeemed() {
        return notificationRedeemed;
    }

    public void setRedeemed(boolean redeemed) {
        notificationRedeemed = redeemed;
    }

    @Override
    public int compareTo(@NonNull Notification notification) {
        if (notificationTimestamp.before(notification.getTimestamp())) {
            return -1;
        } else if (notificationTimestamp.after(notification.getTimestamp())) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Notification)) {
            return false;
        }

        Notification notification = (Notification) object;
        return notificationId == notification.notificationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, notificationTimestamp, notificationDeviceId);
    }
}
