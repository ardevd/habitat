package no.aegisdynamics.habitat.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.notifications.NotificationsActivity;

public class FcmService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> messageData = remoteMessage.getData();
            showNotification(getString(R.string.fcm_notification_title), messageData.get("data"));
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Create notification channel.
        createFcmNotificationChannel();
    }

    private void createFcmNotificationChannel() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.fcm_channel_title);
            // The user-visible description of the channel.
            String description = getString(R.string.fcm_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(getString(R.string.fcm_channel_id),
                    name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.CYAN);
            mChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void showNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), getString(R.string.fcm_channel_id))
                        .setSmallIcon(R.mipmap.ic_app_notification)
                        .setColor(getColor(R.color.colorPrimary))
                        .setContentTitle(title)
                        .setContentText(text)
                        .setLights(ContextCompat.getColor(getApplicationContext(),
                                R.color.colorPrimary), 500, 2000)
                        .setChannelId(getString(R.string.fcm_channel_id));

        // Set intent for the notification
        Intent resultIntent = new Intent(getApplicationContext(), NotificationsActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        // Sets a unique ID for the notification
        int mNotificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        // Gets an instance of the NotificationManager service

        // Builds the notification and issues it.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }
}
