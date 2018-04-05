package no.aegisdynamics.habitat.data.automation;

import android.content.Context;
import android.support.annotation.NonNull;


public class AutomationRepositories {

    private AutomationRepositories() {
        // No instance
    }

    private static AutomationsRepository repository = null;

    public synchronized static AutomationsRepository getRepository(@NonNull Context context, @NonNull AutomationsServiceApi automationsServiceApi) {
        repository = new ContentProviderAutomationProviderRepository(automationsServiceApi, context);
        return repository;
    }
}
