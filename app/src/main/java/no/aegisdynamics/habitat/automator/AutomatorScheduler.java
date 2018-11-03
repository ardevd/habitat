package no.aegisdynamics.habitat.automator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.data.automation.AutomationsRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Helper class that handles scheduling of Automation alerts.
 */

public class AutomatorScheduler implements AutomatorContract.Scheduler, DeviceDataContract {
    private final AlarmManager alarmMgr;
    private final Context context;

    public AutomatorScheduler(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.context = context;
    }

    @Override
    public void scheduleAutomation(Automation automation) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date = simpleDateFormat.parse(automation.getTrigger());
            Calendar automationCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();
            automationCalendar.setTime(date);
            automationCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));// for 6 hour
            automationCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));// for 0 min
            automationCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));// for 0 sec
            while (automationCalendar.before(currentCalendar)) {
                /*
                 *  Scheduled time is in the past. Adjust time with one day
                 */
                automationCalendar.add(Calendar.DATE, 1);
            }
            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            // Pending Intent request code equals the automation ID.
            Intent intent = new Intent(context.getApplicationContext(), AutomatorAutomationReceiver.class);
            intent.setAction("no.aegisdynamics.habitat.AUTOMATION");
            intent.putExtra("device", automation.getDeviceId());
            intent.putExtra("command", automation.getCommands());
            intent.putExtra("requestCode", automation.getId());
            intent.putExtra("type", automation.getType());
            intent.putExtra("name", automation.getName());
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), automation.getId(), intent, 0);
            if (automation.getType().equals(AUTOMATION_TYPE_RECURRING)) {
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, automationCalendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
            } else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, automationCalendar.getTimeInMillis(),
                        alarmIntent);
            }
        } catch (ParseException ex) {
            // Unable to parse trigger data.
        }
    }


    @Override
    public void scheduleAllAutomations(@NonNull AutomationsRepository automationsRepository) {
        automationsRepository.getAutomations(new AutomationsRepository.LoadAutomationsCallback() {

            @Override
            public void onAutomationsLoaded(List<Automation> automations) {
                // Automations loaded, loop through and schedule alarms.
                for (Automation automation : automations) {
                    scheduleAutomation(automation);
                }
            }

            @Override
            public void onAutomationsLoadError(String error) {
                // Could not load automations. Show an error of some sort?
            }
        });
    }

    @Override
    public void deleteScheduledAutomation(int automationId) {
        Intent intent = new Intent(context.getApplicationContext(), AutomatorAutomationReceiver.class);
        intent.setAction("no.aegisdynamics.habitat.AUTOMATION");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                automationId, intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

}
