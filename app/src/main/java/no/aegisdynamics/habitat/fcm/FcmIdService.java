package no.aegisdynamics.habitat.fcm;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import com.google.firebase.iid.FirebaseInstanceIdService;

import no.aegisdynamics.habitat.R;

public class FcmIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
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
}
