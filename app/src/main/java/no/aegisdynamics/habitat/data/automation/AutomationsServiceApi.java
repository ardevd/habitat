package no.aegisdynamics.habitat.data.automation;

import android.content.Context;

import java.util.List;

/**
 * Defines an interface to the service API that is used by this application. All automation data requests should
 * be piped through this interface.
 */

interface AutomationsServiceApi {

    interface AutomationsServiceCallback<T> {
        void onLoaded(T automations);
        void onError(String error);
    }

    void getAllAutomations(Context context, AutomationsServiceCallback<List<Automation>> callback);
    void getAutomation(Context context, int automationId, AutomationsServiceCallback<Automation> callback);
    void removeAutomation(Context context, int automationId, AutomationsServiceCallback<Boolean> callback);
    void createAutomation(Context context, Automation automation, AutomationsServiceCallback<Automation> callback);
}
