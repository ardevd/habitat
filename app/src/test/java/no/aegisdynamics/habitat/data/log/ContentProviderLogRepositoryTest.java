package no.aegisdynamics.habitat.data.log;

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

public class ContentProviderLogRepositoryTest {

    private static final Date LOG_DATE = new Date();
    private static final String LOG_TAG = "TAG";
    private static final String LOG_MESSAGE = "Log message";
    private static final int LOG_TYPE = 1;
    private static final int LOG_ID = 1;

    private static final HabitatLog LOG = new HabitatLog(LOG_ID, LOG_DATE, LOG_TAG, LOG_MESSAGE, LOG_TYPE);

    private List<HabitatLog> LOGS;

    @Mock
    Context context;

    @Mock
    LogServiceApi logServiceApi;

    @Mock
    LogRepository.CreateLogCallback mockedCreateLogCallback;

    @Mock
    LogRepository.LoadLogsCallback mockedLoadLogsCallback;

    @Mock
    LogRepository.GetLogCallback mockedGetLogCallback;

    @Mock
    LogRepository.DeleteLogCallback mockedDeleteLogCallback;

    @Mock
    LogRepository.DeleteAllLogsCallback mockedDeleteAllCallback;

    @Captor
    private ArgumentCaptor<LogServiceApi.LogServiceCallback> logServiceCallbackCaptor;

    private LogRepository logRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        logRepository = new ContentProviderLogRepository(logServiceApi, context);
        LOGS = new ArrayList<>();
        LOGS.add(LOG);
    }

    @Test
    public void getLog_success() {
        logRepository.getLog(LOG.getId(), mockedGetLogCallback);

        verify(logServiceApi).getLog(eq(context), eq(LOG.getId()),
                logServiceCallbackCaptor.capture());

        logServiceCallbackCaptor.getValue().onLoaded(LOG);

        verify(mockedGetLogCallback).onLogLoaded(eq(LOG));
    }

    @Test
    public void getLog_error() {
        logRepository.getLog(LOG.getId(), mockedGetLogCallback);

        verify(logServiceApi).getLog(eq(context), eq(LOG.getId()),
                logServiceCallbackCaptor.capture());

        logServiceCallbackCaptor.getValue().onError("error");

        verify(mockedGetLogCallback).onLogLoadError(eq("error"));
    }

    @Test
    public void getAllLogs_success() {
        logRepository.getLogs(mockedLoadLogsCallback);

        verify(logServiceApi).getAllLogs(eq(context), logServiceCallbackCaptor.capture());

        logServiceCallbackCaptor.getValue().onLoaded(LOGS);

        verify(mockedLoadLogsCallback).onLogsLoaded(eq(LOGS));
    }

    @Test
    public void getAllLogs_error() {
        logRepository.getLogs(mockedLoadLogsCallback);

        verify(logServiceApi).getAllLogs(eq(context), logServiceCallbackCaptor.capture());

        logServiceCallbackCaptor.getValue().onError("error");

        verify(mockedLoadLogsCallback).onLogsLoadError(eq("error"));
    }

    @Test
    public void createLog_success() {
        logRepository.createLog(LOG, mockedCreateLogCallback);
        verify(logServiceApi).createLog(eq(context), eq(LOG), logServiceCallbackCaptor.capture());
        logServiceCallbackCaptor.getValue().onLoaded(LOG);
        verify(mockedCreateLogCallback).onLogCreated();
    }

    @Test
    public void createLog_error() {
        logRepository.createLog(LOG, mockedCreateLogCallback);
        verify(logServiceApi).createLog(eq(context), eq(LOG), logServiceCallbackCaptor.capture());
        logServiceCallbackCaptor.getValue().onError("error");
        verify(mockedCreateLogCallback).onLogCreatedError(eq("error"));
    }

    @Test
    public void deleteLog_success() {
        logRepository.deleteLog(LOG.getId(), mockedDeleteLogCallback);
        verify(logServiceApi).deleteLog(eq(context), eq(LOG.getId()), logServiceCallbackCaptor.capture());
        logServiceCallbackCaptor.getValue().onLoaded(true);
        verify(mockedDeleteLogCallback).onLogDeleted();
    }

    @Test
    public void deleteLog_error() {
        logRepository.deleteLog(LOG.getId(), mockedDeleteLogCallback);
        verify(logServiceApi).deleteLog(eq(context), eq(LOG.getId()), logServiceCallbackCaptor.capture());
        logServiceCallbackCaptor.getValue().onError("error");
        verify(mockedDeleteLogCallback).onLogDeleteError(eq("error"));
    }

    @Test
    public void deleteAllLogs_success() {
        logRepository.deleteAllLogs(mockedDeleteAllCallback);
        verify(logServiceApi).deleteAllLogs(eq(context), logServiceCallbackCaptor.capture());
        logServiceCallbackCaptor.getValue().onLoaded(30);
        verify(mockedDeleteAllCallback).onLogsDeleted(eq(30));
    }

    @Test
    public void deleteAllLogs_error() {
        logRepository.deleteAllLogs(mockedDeleteAllCallback);
        verify(logServiceApi).deleteAllLogs(eq(context), logServiceCallbackCaptor.capture());
        logServiceCallbackCaptor.getValue().onError("error");
        verify(mockedDeleteAllCallback).onLogsDeleteError(eq("error"));
    }
}
