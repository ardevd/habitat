package no.aegisdynamics.habitat.automator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import no.aegisdynamics.habitat.data.Injection;

/**
 * Broadcast receiver that handles scheduled automation alarms
 */

public class AutomatorOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Device has now booted and we need to register all the automation alarms.
            AutomatorScheduler automatorScheduler = new AutomatorScheduler(context);
            automatorScheduler.scheduleAllAutomations(Injection.provideAutomationsRepository(context));
        }

    }
}
