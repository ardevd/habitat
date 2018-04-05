package no.aegisdynamics.habitat.util;

import java.util.Comparator;

import no.aegisdynamics.habitat.data.notifications.Notification;

/**
 * Compares notification timestamps
 */

public class NotificationTimestampComparator implements Comparator<Notification> {

    @Override
    public int compare(Notification nOne, Notification nTwo) {
        return nTwo.getTimestamp().compareTo(nOne.getTimestamp());
    }
}
