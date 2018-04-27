package no.aegisdynamics.habitat.log;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import no.aegisdynamics.habitat.data.log.HabitatLog;
import no.aegisdynamics.habitat.data.log.LogRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the LogPresenter.
 */
public class LogPresenterTest {

    @Mock
    private LogRepository mLogRepository;

    @Mock
    private LogContract.View mLogsView;

    @Captor
    private ArgumentCaptor<LogRepository.LoadLogsCallback> mLoadLogsCallbackCaptor;

    @Captor
    private ArgumentCaptor<LogRepository.DeleteLogCallback> mDeleteLogCallbackCaptor;

    @Captor
    private ArgumentCaptor<LogRepository.DeleteAllLogsCallback> mDeleteLogsCallbackCaptor;

    private LogPresenter mLogPresenter;

    private List<HabitatLog> LOGS;

    private static final Date LOG_DATE = new Date();
    private static final String LOG_TAG = "TAG";
    private static final String LOG_MESSAGE = "Log message";
    private static final int LOG_TYPE = 1;
    private static final int LOG_ID = 1;

    private static final HabitatLog LOG = new HabitatLog(LOG_ID, LOG_DATE, LOG_TAG, LOG_MESSAGE, LOG_TYPE);

    @Before
    public void setupLogPresenter() {
        MockitoAnnotations.initMocks(this);

        mLogPresenter = new LogPresenter(mLogRepository, mLogsView);

        LOGS = new ArrayList<>();
        LOGS.add(LOG);
    }

    @Test
    public void loadLogsFromRepositoryAndIntoView() {
        mLogPresenter.loadLogs();
        verify(mLogsView).setProgressIndicator(eq(true));
        verify(mLogRepository).getLogs(mLoadLogsCallbackCaptor.capture());
        mLoadLogsCallbackCaptor.getValue().onLogsLoaded(LOGS);

        verify(mLogsView).setProgressIndicator(eq(false));
        verify(mLogsView).showLogs(eq(LOGS));
    }

    @Test
    public void loadLogsFromRepositoryAndShowError() {
        mLogPresenter.loadLogs();
        verify(mLogsView).setProgressIndicator(eq(true));
        verify(mLogRepository).getLogs(mLoadLogsCallbackCaptor.capture());
        mLoadLogsCallbackCaptor.getValue().onLogsLoadError("Error");

        verify(mLogsView).setProgressIndicator(eq(false));
        verify(mLogsView).showLogsLoadError(eq("Error"));
    }

    @Test
    public void deleteLogAndShowConfirmation() {
        mLogPresenter.deleteLog(LOG);
        verify(mLogsView).setProgressIndicator(eq(true));
        verify(mLogRepository).deleteLog(eq(LOG.getId()), mDeleteLogCallbackCaptor.capture());
        mDeleteLogCallbackCaptor.getValue().onLogDeleted();

        verify(mLogsView).setProgressIndicator(eq(false));
        verify(mLogsView).showLogDeleted();
    }

    @Test
    public void deleteLogAndShowError() {
        mLogPresenter.deleteLog(LOG);
        verify(mLogsView).setProgressIndicator(eq(true));
        verify(mLogRepository).deleteLog(eq(LOG.getId()), mDeleteLogCallbackCaptor.capture());
        mDeleteLogCallbackCaptor.getValue().onLogDeleteError("Error");

        verify(mLogsView).setProgressIndicator(eq(false));
        verify(mLogsView).showLogDeleteError(eq("Error"));
    }

    @Test
    public void deleteAllLogsShowSuccess() {
        mLogPresenter.deleteAllLogs();
        verify(mLogsView).setProgressIndicator(eq(true));
        verify(mLogRepository).deleteAllLogs(mDeleteLogsCallbackCaptor.capture());
        mDeleteLogsCallbackCaptor.getValue().onLogsDeleted(21);

        verify(mLogsView).setProgressIndicator(eq(false));
        verify(mLogsView).showLogsDeleted(eq(21));
    }

    @Test
    public void deleteAllLogsShowError() {
        mLogPresenter.deleteAllLogs();
        verify(mLogsView).setProgressIndicator(eq(true));
        verify(mLogRepository).deleteAllLogs(mDeleteLogsCallbackCaptor.capture());
        mDeleteLogsCallbackCaptor.getValue().onLogsDeleteError("error");

        verify(mLogsView).setProgressIndicator(eq(false));
        verify(mLogsView).showLogsDeletedError(eq("error"));
    }
}
