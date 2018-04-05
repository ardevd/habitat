package no.aegisdynamics.habitat.backup;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import no.aegisdynamics.habitat.data.backup.Backup;
import no.aegisdynamics.habitat.data.backup.BackupsRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the BackupPresenter.
 */

public class BackupPresenterTest {

    private static List<Backup> BACKUPS = Lists.newArrayList(new Backup("backup.zab", 1167548,
            new Date()), new Backup("backup2.zab", 1252123, new Date()));

    @Mock
    private BackupsRepository mBackupsRepository;

    @Mock
    private BackupContract.View mBackupView;

    @Captor
    private ArgumentCaptor<BackupsRepository.LoadBackupsCallback> mLoadBackupsCallbackCaptor;

    @Captor
    private ArgumentCaptor<BackupsRepository.DeleteBackupCallback> mDeleteBackupsCallbackCaptor;

    @Captor
    private ArgumentCaptor<BackupsRepository.CreateBackupCallback> mCreateBackupCallbackCaptor;

    private BackupPresenter mBackupPresenter;

    @Before
    public void setupBackupPresenter() {
        MockitoAnnotations.initMocks(this);
        mBackupPresenter = new BackupPresenter(mBackupsRepository, mBackupView);
    }

    @Test
    public void loadBackupsFromRepositoryAndShowInView() {
        // When loading backups is requested.
        mBackupPresenter.loadBackups();

        // Callback is captured and invoked.
        verify(mBackupView).setProgressIndicator(true);
        verify(mBackupsRepository).getBackups(mLoadBackupsCallbackCaptor.capture());
        mLoadBackupsCallbackCaptor.getValue().onBackupsLoaded(BACKUPS);

        // Progress indicator is hidden and backups are shown in the UI
        verify(mBackupView).setProgressIndicator(false);
        verify(mBackupView).showBackupsLoaded(BACKUPS);
    }

    @Test
    public void loadBackupsFromRepositoryAndShowErrorInView() {
        // When loading backups is requested.
        mBackupPresenter.loadBackups();

        // Callback is captured and invoked.
        verify(mBackupView).setProgressIndicator(true);
        verify(mBackupsRepository).getBackups(mLoadBackupsCallbackCaptor.capture());
        mLoadBackupsCallbackCaptor.getValue().onBackupsLoadError("Error");

        // Progress indicator is hidden and backups are shown in the UI
        verify(mBackupView).setProgressIndicator(false);
        verify(mBackupView).showBackupCreatedError(eq("Error"));
    }

    @Test
    public void deleteBackup() {
        mBackupPresenter.deleteBackup(BACKUPS.get(1));

        verify(mBackupView).setProgressIndicator(true);
        verify(mBackupsRepository).deleteBackup(eq(BACKUPS.get(1)), mDeleteBackupsCallbackCaptor.capture());
        mDeleteBackupsCallbackCaptor.getValue().backupDeleted();

        verify(mBackupView).setProgressIndicator(false);
        verify(mBackupView).showBackupDeleted();
    }

    @Test
    public void deleteBackup_showError() {
        mBackupPresenter.deleteBackup(BACKUPS.get(1));

        verify(mBackupView).setProgressIndicator(true);
        verify(mBackupsRepository).deleteBackup(eq(BACKUPS.get(1)), mDeleteBackupsCallbackCaptor.capture());
        mDeleteBackupsCallbackCaptor.getValue().backupDeleteError("Error");

        verify(mBackupView).setProgressIndicator(false);
        verify(mBackupView).showBackupDeleteError(eq("Error"));
    }

    @Test
    public void createBackup_showSuccess() {
        mBackupPresenter.createBackup();

        verify(mBackupView).showBackupStarted();
        verify(mBackupsRepository).createBackup(eq(true), mCreateBackupCallbackCaptor.capture());
        mCreateBackupCallbackCaptor.getValue().onBackupCreated();

        verify(mBackupView).showBackupCreated();
    }

    @Test
    public void createBackup_showError() {
        mBackupPresenter.createBackup();

        verify(mBackupView).showBackupStarted();
        verify(mBackupsRepository).createBackup(eq(true), mCreateBackupCallbackCaptor.capture());
        mCreateBackupCallbackCaptor.getValue().onBackupCreatedError("Error");

        verify(mBackupView).showBackupCreatedError(eq("Error"));
    }

}
