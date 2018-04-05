package no.aegisdynamics.habitat.backup;

import java.util.List;

import no.aegisdynamics.habitat.data.backup.Backup;

/**
 * Specifies the contract between the Backup view and presenter.
 */

public interface BackupContract {

    interface View {
        void showBackupStarted();
        void showBackupCreated();
        void showBackupCreatedError(String error);
        void showBackupsLoaded(List<Backup> backups);
        void showBackupDeleted();
        void showBackupDeleteError(String error);
        void setProgressIndicator(boolean active);
        void showAvailableStorage(long free, long total);
        void schedulePeriodicBackups();
        void cancelScheduledPeriodicBackups();
        boolean checkForScheduledPeriodicBackups();

    }

    interface UserActionListener {
        void createBackup();
        void loadBackups();
        void deleteBackup(Backup backup);
        void getAvailableStorage();

    }
}
