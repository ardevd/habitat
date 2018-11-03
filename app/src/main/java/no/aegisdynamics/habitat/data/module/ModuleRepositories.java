package no.aegisdynamics.habitat.data.module;

import android.content.Context;
import android.support.annotation.NonNull;

public class ModuleRepositories {

    private ModuleRepositories() {
        //No instance
    }

    public static synchronized ModulesRepository getRepository(@NonNull Context context,
                                                               @NonNull ModuleServiceApi moduleServiceApi) {
        return new APIModulesRepository(moduleServiceApi, context);
    }
}
