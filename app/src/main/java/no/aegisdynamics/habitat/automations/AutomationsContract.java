package no.aegisdynamics.habitat.automations;


import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.automation.Automation;


public interface AutomationsContract {

    interface Activity {
        void showFabHint();
    }

    interface View {
        void showAutomations(List<Automation> automations);
        void showAutomationsLoadError(String error);
        void showDeleteAutomation();
        void showDeleteAutomationError(String error);
        void showAddAutomation();
        void setProgressIndicator(boolean active);
        void showAutomationDetailUI(int automationId);
    }
    interface UserActionsListener {
        void loadAutomations();
        void deleteAutomation(Automation automation);
        void addAutomation();
        void openAutomationDetails(@NonNull Automation requestedAutomation);
    }
}
