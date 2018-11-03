package no.aegisdynamics.habitat.data.log;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.provider.DeviceDataContract;

import static no.aegisdynamics.habitat.data.log.LogServiceApiHelper.getLogFromCursorEntry;

/**
 * Implementation of the Log Service API that communicates with the Content Provider
 */
public class LogServiceApiImpl implements LogServiceApi, DeviceDataContract {
    @Override
    public void getAllLogs(Context context, LogServiceCallback<List<HabitatLog>> callback) {
        Cursor logsCursor = context.getContentResolver().query(CONTENT_URI_LOGS, null, null,
                null, FIELD_ID + " DESC");
        List<HabitatLog> logs = new ArrayList<>();

        while(logsCursor.moveToNext()) {
            logs.add(getLogFromCursorEntry(logsCursor));
        }

        logsCursor.close();
        callback.onLoaded(logs);
    }

    @Override
    public void getLog(Context context, long logId, LogServiceCallback<HabitatLog> callback) {
        Cursor logCursor = context.getContentResolver().query(CONTENT_URI_LOGS, null,
                FIELD_ID + "= ?", new String[]{String.valueOf(logId)}, null);
        logCursor.moveToFirst();
        callback.onLoaded(getLogFromCursorEntry(logCursor));
    }

    @Override
    public void deleteLog(Context context, long logId, LogServiceCallback<Boolean> callback) {
        ContentResolver res = context.getContentResolver();
        String[] args = new String[] {String.valueOf(logId)};
        int deletedCount = res.delete(CONTENT_URI_LOGS, "_ID=?", args);
        if (deletedCount > 0) {
            callback.onLoaded(true);
        } else {
            callback.onError("No log entry was deleted");
        }
    }

    @Override
    public void deleteAllLogs(Context context, LogServiceCallback<Integer> callback) {
        ContentResolver res = context.getContentResolver();
        int deletedCount = res.delete(CONTENT_URI_LOGS, null, null);
        if (deletedCount > 0) {
            callback.onLoaded(deletedCount);
        } else {
            callback.onError("No log entry was deleted");
        }
    }

    @Override
    public void createLog(Context context, HabitatLog log, LogServiceCallback<HabitatLog> callback) {
        ContentValues values = new ContentValues();
        values.put(FIELD_LOG_TAG, log.getTag());
        values.put(FIELD_LOG_MESSAGE, log.getMessage());
        values.put(FIELD_LOG_TYPE, log.getType());
        if (context != null) {
            long logId = ContentUris.parseId(context.getContentResolver().insert(CONTENT_URI_LOGS, values));
            if (logId > 0) {
                callback.onLoaded(log);
            } else {
                callback.onError("Error occurred when adding log entry");
            }
            callback.onError("Invalid context when adding log entry");
        }
    }
}
