package no.aegisdynamics.habitat.data.backup;

import android.content.Context;
import android.support.annotation.NonNull;

public class BackupRepositories {

    private BackupRepositories() {
        // No instance
    }

    public static synchronized BackupsRepository getRepository(@NonNull Context context,
                                                               @NonNull BackupsServiceApi backupsServiceApi) {
        return new APIBackupProviderRepository(backupsServiceApi, context);
    }
}
