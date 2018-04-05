package no.aegisdynamics.habitat.backup;

import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import no.aegisdynamics.habitat.data.backup.Backup;
import no.aegisdynamics.habitat.data.backup.BackupsRepository;

/**
 * Listens to user actions from the UI ({@link BackupFragment}), retrieves the data and updates the
 * UI as required.
 */

public class BackupPresenter implements BackupContract.UserActionListener {

    private final BackupContract.View mBackupView;
    private final BackupsRepository mBackupRepository;

    public BackupPresenter(@NonNull BackupsRepository backupsRepository, @NonNull BackupContract.View backupView) {
        mBackupView = backupView;
        mBackupRepository = backupsRepository;
    }

    @Override
    public void createBackup() {
        mBackupView.showBackupStarted();
        mBackupRepository.createBackup(true, new BackupsRepository.CreateBackupCallback() {
            @Override
            public void onBackupCreated() {
                mBackupView.showBackupCreated();
            }

            @Override
            public void onBackupCreatedError(String error) {
                mBackupView.showBackupCreatedError(error);
            }
        });

    }

    @Override
    public void loadBackups() {
        mBackupView.setProgressIndicator(true);
        mBackupRepository.getBackups(new BackupsRepository.LoadBackupsCallback() {
            @Override
            public void onBackupsLoaded(List<Backup> backups) {
                mBackupView.setProgressIndicator(false);
                mBackupView.showBackupsLoaded(backups);
            }

            @Override
            public void onBackupsLoadError(String error) {
                mBackupView.setProgressIndicator(false);
                mBackupView.showBackupCreatedError(error);
            }
        });

    }

    @Override
    public void deleteBackup(Backup backup) {
        mBackupView.setProgressIndicator(true);
        mBackupRepository.deleteBackup(backup, new BackupsRepository.DeleteBackupCallback() {
            @Override
            public void backupDeleted() {
                mBackupView.setProgressIndicator(false);
                mBackupView.showBackupDeleted();
            }

            @Override
            public void backupDeleteError(String error) {
                mBackupView.setProgressIndicator(false);
                mBackupView.showBackupDeleteError(error);
            }
        });
    }

    @Override
    public void getAvailableStorage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableStorage = blockSize * stat.getAvailableBlocksLong();
        long totalStorage = blockSize * stat.getBlockCountLong();
        mBackupView.showAvailableStorage(availableStorage, totalStorage);
    }
}
