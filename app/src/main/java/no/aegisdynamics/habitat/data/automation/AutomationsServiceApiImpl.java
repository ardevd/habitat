package no.aegisdynamics.habitat.data.automation;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.automator.AutomatorOnBootReceiver;
import no.aegisdynamics.habitat.automator.AutomatorScheduler;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Implementation of the Automation Service API that communicates with the Content Provider
 */
public class AutomationsServiceApiImpl implements AutomationsServiceApi, DeviceDataContract {
    
    @Override
    public void getAllAutomations(Context context, AutomationsServiceCallback<List<Automation>> callback) {
        Cursor automationsCursor = context.getContentResolver().query(CONTENT_URI_AUTOMATIONS, null, null, null, FIELD_ID + " DESC");
        List<Automation> listAutomations = new ArrayList<>();

        while(automationsCursor.moveToNext()) {
            listAutomations.add(getAutomationFromCursorEntry(automationsCursor));
        }
        automationsCursor.close();

        if (listAutomations.size() == 0) {
            // No automations available, lets disable the ON_BOOT_COMPLETE receiver.
            ComponentName receiver = new ComponentName(context, AutomatorOnBootReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }

        callback.onLoaded(listAutomations);
    }

    @Override
    public void getAutomation(Context context, int automationId, AutomationsServiceCallback<Automation> callback) {
        Cursor automationCursor = context.getContentResolver().query(CONTENT_URI_AUTOMATIONS, null, FIELD_ID + "= ?", new String[]{String.valueOf(automationId)}, null);
        automationCursor.moveToFirst();
        callback.onLoaded(getAutomationFromCursorEntry(automationCursor));
    }

    @Override
    public void removeAutomation(Context context, int automationId, AutomationsServiceCallback<Boolean> callback) {
        ContentResolver res = context.getContentResolver();
        String[] args = new String[] {String.valueOf(automationId)};
        res.delete(CONTENT_URI_AUTOMATIONS, "_ID=?", args);
        callback.onLoaded(true);
        // Automation deleted. Delete scheduled alarm
        AutomatorScheduler as = new AutomatorScheduler(context);
        as.deleteScheduledAutomation(automationId);
    }

    @Override
    public void createAutomation(Context context, Automation automation, AutomationsServiceCallback<Automation> callback) {
        ContentValues values = new ContentValues();
        values.put(FIELD_AUTOMATION_NAME, automation.getName());
        values.put(FIELD_AUTOMATION_DESCRIPTION, automation.getDescription());
        values.put(FIELD_AUTOMATION_TYPE, automation.getType());
        values.put(FIELD_AUTOMATION_TRIGGER, automation.getTrigger());
        values.put(FIELD_AUTOMATION_COMMANDS, automation.getCommands());
        values.put(FIELD_AUTOMATION_DEVICE, automation.getDeviceId());
        long automationId = ContentUris.parseId(context.getContentResolver().insert(CONTENT_URI_AUTOMATIONS, values));
        if (automationId > 0) {
            // Automation has been added, enable the ON_BOOT_COMPLETE receiver.
            ComponentName receiver = new ComponentName(context, AutomatorOnBootReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            callback.onLoaded(automation);

            // Schedule alarm
            AutomatorScheduler as = new AutomatorScheduler(context);
            automation.setId((int)automationId);
            as.scheduleAutomation(automation);

        } else {
            callback.onError("Error");
        }
    }

    private Automation getAutomationFromCursorEntry(Cursor automationsCursor) {
        int columnAutomationId = automationsCursor.getColumnIndex(FIELD_ID);
        int columnAutomationName = automationsCursor.getColumnIndex(FIELD_AUTOMATION_NAME);
        int columnAutomationDescription = automationsCursor.getColumnIndex(FIELD_AUTOMATION_DESCRIPTION);
        int columnAutomationType = automationsCursor.getColumnIndex(FIELD_AUTOMATION_TYPE);
        int columnAutomationTrigger = automationsCursor.getColumnIndex(FIELD_AUTOMATION_TRIGGER);
        int columnAutomationCommands = automationsCursor.getColumnIndex(FIELD_AUTOMATION_COMMANDS);
        int columnAutomationDevice = automationsCursor.getColumnIndex(FIELD_AUTOMATION_DEVICE);


        return new Automation(automationsCursor.getInt(columnAutomationId),
                automationsCursor.getString(columnAutomationName),
                automationsCursor.getString(columnAutomationDescription),
                automationsCursor.getString(columnAutomationType),
                automationsCursor.getString(columnAutomationTrigger),
                automationsCursor.getString(columnAutomationCommands),
                automationsCursor.getString(columnAutomationDevice));
    }
}
