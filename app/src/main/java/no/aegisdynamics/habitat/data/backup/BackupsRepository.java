package no.aegisdynamics.habitat.data.backup;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing backup data
 */

public interface BackupsRepository {

    interface LoadBackupsCallback {
        void onBackupsLoaded(List<Backup> backups);
        void onBackupsLoadError(String error);
    }

    interface CreateBackupCallback {
        void onBackupCreated();
        void onBackupCreatedError(String error);
    }

    interface DeleteBackupCallback {
        void backupDeleted();
        void backupDeleteError(String error);
    }

    void getBackups(@NonNull LoadBackupsCallback callback);
    void createBackup(boolean manualBackup, @NonNull CreateBackupCallback callback);
    void deleteBackup(@NonNull Backup backup, DeleteBackupCallback callback);
}
