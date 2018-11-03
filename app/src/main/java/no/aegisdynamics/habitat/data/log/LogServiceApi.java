package no.aegisdynamics.habitat.data.log;


import android.content.Context;

import java.util.List;

interface LogServiceApi {

    interface LogServiceCallback<T> {
        void onLoaded(T logs);
        void onError(String error);
    }

    void getAllLogs(Context context, LogServiceCallback<List<HabitatLog>> callback);
    void getLog(Context context, long logId, LogServiceCallback<HabitatLog> callback);
    void deleteLog(Context context, long logId, LogServiceCallback<Boolean> callback);
    void deleteAllLogs(Context context, LogServiceCallback<Integer> callback);
    void createLog(Context context, HabitatLog log, LogServiceCallback<HabitatLog> callback);
}
