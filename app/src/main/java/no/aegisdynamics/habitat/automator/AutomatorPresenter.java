package no.aegisdynamics.habitat.automator;


import android.support.annotation.NonNull;

import no.aegisdynamics.habitat.data.device.DevicesRepository;

public class AutomatorPresenter implements AutomatorContract.UserActionsListener {

    private final DevicesRepository mDevicesRepository;
    private final AutomatorContract.Notifier mNotifier;

    public AutomatorPresenter(@NonNull DevicesRepository devicesRepository, @NonNull AutomatorContract.Notifier notifier) {
        mDevicesRepository = devicesRepository;
        mNotifier = notifier;
    }

    @Override
    public void sendAutomatedCommand(@NonNull String deviceId, @NonNull String automationTitle, @NonNull String command) {
        // Send device command to server.
        mDevicesRepository.sendAutomatedCommand(deviceId, command, automationTitle, new DevicesRepository.SendAutomatedCommandCallback() {


            @Override
            public void onAutomatedCommandSent(String automationTitle) {
                mNotifier.showCommandSuccess(automationTitle);
            }

            @Override
            public void onAutomatedCommandFailed(String error, String automationTitle) {
                mNotifier.showCommandFailed(error, automationTitle);
            }

        });
    }
}
