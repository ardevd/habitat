package no.aegisdynamics.habitat.data.log;

import android.content.Context;
import android.support.annotation.NonNull;

public class LogRepositories {

    private LogRepositories() {
        // No Instance
    }

    public static synchronized LogRepository getRepository(@NonNull Context context,
                                                           @NonNull LogServiceApi logServiceApi) {
        return new ContentProviderLogRepository(logServiceApi, context);
    }
}
