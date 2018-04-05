package no.aegisdynamics.habitat.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.backup.BackupActivity;

/**
 * Helper class for backup related notifications
 */

public class BackupNotifications {

    private final static String NOTIFICATION_CHANNEL_ID_BACKUPS = "habitat_backups";
    private final static int NOTIFICATION_NOTIFICATION_REQUEST_CODE = 1;
    private Context mContext;

    public BackupNotifications(Context context) {
        mContext = context;
    }

    public void showNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_BACKUPS)
                        .setSmallIcon(R.mipmap.ic_app_notification)
                        .setColor(mContext.getColor(R.color.colorPrimary))
                        .setContentTitle(title)
                        .setContentText(text)
                        .setChannelId(NOTIFICATION_CHANNEL_ID_BACKUPS);

        // Set intent for the notification
        Intent resultIntent = new Intent(mContext, BackupActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        NOTIFICATION_NOTIFICATION_REQUEST_CODE,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        // Sets a unique ID for the notification
        int mNotificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        // Gets an instance of the NotificationManager service

        // Builds the notification and issues it.
        createNotificationChannel().notify(mNotificationId, mBuilder.build());
    }

    private NotificationManager createNotificationChannel() {
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // The user-visible name of the channel.
            CharSequence name = mContext.getString(R.string.backup_notification_channel_title);
            // The user-visible description of the channel.
            String description = mContext.getString(R.string.backup_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_BACKUPS, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(R.color.colorPrimary);
            mChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        return mNotificationManager;
    }
}
