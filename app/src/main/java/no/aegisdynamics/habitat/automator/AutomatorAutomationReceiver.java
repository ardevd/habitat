package no.aegisdynamics.habitat.automator;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.automation.AutomationsRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

public class AutomatorAutomationReceiver extends BroadcastReceiver implements DeviceDataContract {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("no.aegisdynamics.habitat.AUTOMATION")) {
            // Alarm received. Execute automation
            String deviceId = intent.getStringExtra("device");
            String command = intent.getStringExtra("command");
            String type = intent.getStringExtra("type");
            String name = intent.getStringExtra("name");
            int automationId = intent.getIntExtra("requestCode", 0);
            if (type.equals(AUTOMATION_TYPE_SINGLE)) {
                // Single shot automation. Delete automation
                deleteAutomation(automationId, Injection.provideAutomationsRepository(context));
            }
            AutomatorPresenter mActionsListener = new AutomatorPresenter(Injection.provideDevicesRepository(context), new AutomatorNotificator(context));
            mActionsListener.sendAutomatedCommand(deviceId, name, command);
        }
    }

    private void deleteAutomation(int automationId, AutomationsRepository mAutomationsRepository) {
        mAutomationsRepository.deleteAutomation(automationId, new AutomationsRepository.DeleteAutomationCallback(){

            @Override
            public void onAutomationDeleted() {
            }

            @Override
            public void onAutomationDeleteError(String error) {
            }
        });
    }

}
