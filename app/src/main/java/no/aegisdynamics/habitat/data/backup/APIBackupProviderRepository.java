package no.aegisdynamics.habitat.data.backup;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

public class APIBackupProviderRepository implements BackupsRepository {

    private final BackupsServiceApi mBackupsServiceApi;
    private final Context mContext;

    public APIBackupProviderRepository(@NonNull BackupsServiceApi backupServiceApi,
                                       @NonNull Context context) {
        mBackupsServiceApi = backupServiceApi;
        mContext = context;
    }

    @Override
    public void getBackups(@NonNull final LoadBackupsCallback callback) {
        mBackupsServiceApi.getBackups(mContext, new BackupsServiceApi.BackupsServiceCallback<List<Backup>>() {
            @Override
            public void onLoaded(List<Backup> backups) {
                callback.onBackupsLoaded(backups);
            }

            @Override
            public void onError(String error) {
                callback.onBackupsLoadError(error);
            }
        });

    }

    @Override
    public void createBackup(boolean manualBackup, @NonNull final CreateBackupCallback callback) {
        mBackupsServiceApi.createBackup(mContext, manualBackup, new BackupsServiceApi.BackupsServiceCallback<Boolean>() {
            @Override
            public void onLoaded(Boolean backups) {
                callback.onBackupCreated();
            }

            @Override
            public void onError(String error) {
                callback.onBackupCreatedError(error);
            }
        });
    }

    @Override
    public void deleteBackup(@NonNull Backup backup, final DeleteBackupCallback callback) {
        mBackupsServiceApi.deleteBackup(mContext, backup, new BackupsServiceApi.BackupsServiceCallback<Boolean>() {

            @Override
            public void onLoaded(Boolean backups) {
                callback.backupDeleted();
            }

            @Override
            public void onError(String error) {
                callback.backupDeleteError(error);
            }
        });
    }
}
