package no.aegisdynamics.habitat.automator;


import android.support.annotation.NonNull;

import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.data.automation.AutomationsRepository;

public interface AutomatorContract {

    interface Scheduler {
        void scheduleAutomation(Automation automation);
        void scheduleAllAutomations(AutomationsRepository automationsRepository);
        void deleteScheduledAutomation(int automationId);
    }

    interface Notifier {
        void showCommandSuccess(String automationTitle);
        void showCommandFailed(String error, String automationTitle);
    }

    interface UserActionsListener {
        void sendAutomatedCommand(@NonNull String deviceId, @NonNull String automationTitle, @NonNull String command);
    }

}
