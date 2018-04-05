package no.aegisdynamics.habitat.data.automation;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing automation data.
 */

public interface AutomationsRepository {

    interface LoadAutomationsCallback {

        void onAutomationsLoaded(List<Automation> automations);

        void onAutomationsLoadError(String error);
    }

    interface GetAutomationCallback {

        void onAutomationLoaded(Automation automation);

        void onAutomationLoadError(String error);
    }

    interface DeleteAutomationCallback {
        void onAutomationDeleted();
        void onAutomationDeleteError(String error);
    }

    interface CreateAutomationCallback {
        void onAutomationCreated();
        void onAutomationCreateError(String error);
    }

    void getAutomations(@NonNull LoadAutomationsCallback callback);

    void getAutomation(int automationId, @NonNull GetAutomationCallback callback);

    void createAutomation(@NonNull Automation mAutomation, @NonNull CreateAutomationCallback callback);

    void deleteAutomation(int automationId, DeleteAutomationCallback callback);
}
