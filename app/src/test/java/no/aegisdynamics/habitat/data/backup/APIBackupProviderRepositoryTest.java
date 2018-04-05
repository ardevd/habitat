package no.aegisdynamics.habitat.data.backup;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the APIBackupProviderRepository class
 */
public class APIBackupProviderRepositoryTest {

    private final static Backup BACKUP = new Backup("backupfile", 1041241, new Date());
    private List<Backup> BACKUPS;

    @Mock
    Context context;

    @Mock
    BackupsServiceApi serviceApi;

    @Mock
    BackupsRepository.CreateBackupCallback mockedCreateBackupCallback;

    @Mock
    BackupsRepository.DeleteBackupCallback mockedDeleteBackupCallback;

    @Mock
    BackupsRepository.LoadBackupsCallback mockedLoadBackupsCallback;

    @Captor
    private ArgumentCaptor<BackupsServiceApi.BackupsServiceCallback> backupServiceCallbackCaptor;

    private BackupsRepository backupsRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        backupsRepository = new APIBackupProviderRepository(serviceApi, context);
        BACKUPS = new ArrayList<>();
        BACKUPS.add(BACKUP);
    }

    @Test
    public void getBackups_success() {
        backupsRepository.getBackups(mockedLoadBackupsCallback);
        verify(serviceApi).getBackups(eq(context), backupServiceCallbackCaptor.capture());
        backupServiceCallbackCaptor.getValue().onLoaded(BACKUPS);

        verify(mockedLoadBackupsCallback).onBackupsLoaded(eq(BACKUPS));
    }

    @Test
    public void getBackups_error() {
        backupsRepository.getBackups(mockedLoadBackupsCallback);
        verify(serviceApi).getBackups(eq(context), backupServiceCallbackCaptor.capture());
        backupServiceCallbackCaptor.getValue().onError("Error");

        verify(mockedLoadBackupsCallback).onBackupsLoadError(eq("Error"));
    }

    @Test
    public void createManualBackup_success() {
        backupsRepository.createBackup(true, mockedCreateBackupCallback);
        verify(serviceApi).createBackup(eq(context), eq(true), backupServiceCallbackCaptor.capture());
        backupServiceCallbackCaptor.getValue().onLoaded(true);

        verify(mockedCreateBackupCallback).onBackupCreated();
    }

    @Test
    public void createManualBackup_error() {
        backupsRepository.createBackup(true, mockedCreateBackupCallback);
        verify(serviceApi).createBackup(eq(context), eq(true), backupServiceCallbackCaptor.capture());
        backupServiceCallbackCaptor.getValue().onError("error");

        verify(mockedCreateBackupCallback).onBackupCreatedError("error");
    }

    @Test
    public void deleteBackup_success() {
        backupsRepository.deleteBackup(BACKUP, mockedDeleteBackupCallback);
        verify(serviceApi).deleteBackup(eq(context), eq(BACKUP), backupServiceCallbackCaptor.capture());
        backupServiceCallbackCaptor.getValue().onLoaded(true);

        verify(mockedDeleteBackupCallback).backupDeleted();
    }

    @Test
    public void deleteBackup_error() {
        backupsRepository.deleteBackup(BACKUP, mockedDeleteBackupCallback);
        verify(serviceApi).deleteBackup(eq(context), eq(BACKUP), backupServiceCallbackCaptor.capture());
        backupServiceCallbackCaptor.getValue().onError("error");

        verify(mockedDeleteBackupCallback).backupDeleteError(eq("error"));
    }
}
