package no.aegisdynamics.habitat.data.backup;


import android.content.Context;

import java.util.List;

/**
 * Defines an interface to the backup service API.
 */
interface BackupsServiceApi {

    interface BackupsServiceCallback<T> {
        void onLoaded(T backups);
        void onError(String error);
    }

    void getBackups(Context context, BackupsServiceApi.BackupsServiceCallback<List<Backup>> callback);
    void createBackup(Context context, boolean manualBackup, BackupsServiceApi.BackupsServiceCallback<Boolean> callback);
    void deleteBackup(Context context, Backup backup, BackupsServiceApi.BackupsServiceCallback<Boolean> callback);
}
