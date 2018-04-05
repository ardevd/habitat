package no.aegisdynamics.habitat.itemListeners;

import no.aegisdynamics.habitat.data.backup.Backup;

/**
 * Item listener for the backup adapter.
 */

public interface BackupItemListener {
    void onBackupClick(Backup clickedBackup);
    void onBackupDeleteClick(Backup clickedBackup);
}
