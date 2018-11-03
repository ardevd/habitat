package no.aegisdynamics.habitat.data.automation;

import android.content.Context;
import android.support.annotation.NonNull;


public class AutomationRepositories {

    private AutomationRepositories() {
        // No instance
    }

    public static synchronized AutomationsRepository getRepository(@NonNull Context context, @NonNull AutomationsServiceApi automationsServiceApi) {
        return new ContentProviderAutomationProviderRepository(automationsServiceApi, context);
    }
}
