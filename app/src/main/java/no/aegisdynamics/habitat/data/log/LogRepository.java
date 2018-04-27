package no.aegisdynamics.habitat.data.log;

import android.support.annotation.NonNull;

import java.util.List;

public interface LogRepository {

    interface LoadLogsCallback {
        void onLogsLoaded(List<HabitatLog> logs);
        void onLogsLoadError(String error);
    }

    interface GetLogCallback {
        void onLogLoaded(HabitatLog log);
        void onLogLoadError(String error);
    }

    interface DeleteLogCallback {
        void onLogDeleted();
        void onLogDeleteError(String error);
    }

    interface DeleteAllLogsCallback {
        void onLogsDeleted(int count);
        void onLogsDeleteError(String error);
    }

    interface CreateLogCallback {
        void onLogCreated();
        void onLogCreatedError(String error);
    }

    void getLogs(@NonNull LoadLogsCallback callback);
    void getLog(long logId, @NonNull GetLogCallback callback);
    void deleteLog(long logId, @NonNull DeleteLogCallback callback);
    void deleteAllLogs(@NonNull DeleteAllLogsCallback callback);
    void createLog(@NonNull HabitatLog log, @NonNull CreateLogCallback callback);
}
