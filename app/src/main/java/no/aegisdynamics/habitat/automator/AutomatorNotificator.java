package no.aegisdynamics.habitat.automator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.automations.AutomationsActivity;

public class AutomatorNotificator implements AutomatorContract.Notifier {

    private final Context mContext;
    private static final String CHANNEL_ID = "habitat_automations";

    AutomatorNotificator(Context context) {
        mContext = context;
    }

    @Override
    public void showCommandSuccess(String automationTitle) {
       showNotification(automationTitle,
               mContext.getString(R.string.automator_notification_success_text));
    }

    @Override
    public void showCommandFailed(String error, String automationTitle) {
        showNotification(automationTitle,
                mContext.getString(R.string.automator_notification_error_title));
    }

    private void showNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_app_notification)
                        .setColor(mContext.getColor(R.color.colorPrimary))
                        .setContentTitle(title)
                        .setContentText(text)
                        .setChannelId(CHANNEL_ID);

        // Set intent for the notification
        Intent resultIntent = new Intent(mContext, AutomationsActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
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
        createNotificationChannel().notify(mNotificationId, mBuilder.build());
    }

    private NotificationManager createNotificationChannel() {
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // The user-visible name of the channel.
            CharSequence name = mContext.getString(R.string.automator_notification_channel_title);
            // The user-visible description of the channel.
            String description = mContext.getString(R.string.automator_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

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
