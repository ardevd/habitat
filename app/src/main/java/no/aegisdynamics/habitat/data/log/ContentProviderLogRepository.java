package no.aegisdynamics.habitat.data.log;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;
/**
 * Concrete implementation to manage log data from the content provider
 */
public class ContentProviderLogRepository implements LogRepository {

    private final LogServiceApi mLogServiceApi;
    private final Context mContext;

    public ContentProviderLogRepository(@NonNull LogServiceApi serviceApi,
                                        @NonNull Context context) {
        mLogServiceApi = serviceApi;
        mContext = context;
    }

    @Override
    public void getLogs(final @NonNull LoadLogsCallback callback) {
        mLogServiceApi.getAllLogs(mContext, new LogServiceApi.LogServiceCallback<List<HabitatLog>>() {
            @Override
            public void onLoaded(List<HabitatLog> logs) {
                callback.onLogsLoaded(logs);
            }

            @Override
            public void onError(String error) {
                callback.onLogsLoadError(error);
            }
        });

    }

    @Override
    public void getLog(long logId, @NonNull final GetLogCallback callback) {
        mLogServiceApi.getLog(mContext, logId, new LogServiceApi.LogServiceCallback<HabitatLog>() {
            @Override
            public void onLoaded(HabitatLog log) {
                callback.onLogLoaded(log);
            }

            @Override
            public void onError(String error) {
                callback.onLogLoadError(error);
            }
        });
    }

    @Override
    public void deleteLog(long logId, @NonNull final DeleteLogCallback callback) {
        mLogServiceApi.deleteLog(mContext, logId, new LogServiceApi.LogServiceCallback<Boolean>() {
            @Override
            public void onLoaded(Boolean success) {
                callback.onLogDeleted();
            }

            @Override
            public void onError(String error) {
                callback.onLogDeleteError(error);
            }
        });

    }

    @Override
    public void deleteAllLogs(@NonNull final DeleteAllLogsCallback callback) {
        mLogServiceApi.deleteAllLogs(mContext, new LogServiceApi.LogServiceCallback<Integer>() {
            @Override
            public void onLoaded(Integer deletedItems) {
                callback.onLogsDeleted(deletedItems);
            }

            @Override
            public void onError(String error) {
                callback.onLogsDeleteError(error);
            }
        });
    }

    @Override
    public void createLog(@NonNull HabitatLog log, @NonNull final CreateLogCallback callback) {
        mLogServiceApi.createLog(mContext, log, new LogServiceApi.LogServiceCallback<HabitatLog>() {
            @Override
            public void onLoaded(HabitatLog log) {
                callback.onLogCreated();
            }

            @Override
            public void onError(String error) {
                callback.onLogCreatedError(error);
            }
        });
    }
}
