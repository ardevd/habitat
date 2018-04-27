package no.aegisdynamics.habitat.data.log;

import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import no.aegisdynamics.habitat.provider.DeviceDataContract;

public class LogServiceApiHelper implements DeviceDataContract {

    private static final String TAG = "HabitatLogServiceAPI";

    private LogServiceApiHelper() {
        // Empty private constructor
    }

    public static HabitatLog getLogFromCursorEntry(Cursor logCursor) {
        int cLogId = logCursor.getColumnIndex(FIELD_ID);
        int cLogTag = logCursor.getColumnIndex(FIELD_LOG_TAG);
        int cLogMessage = logCursor.getColumnIndex(FIELD_LOG_MESSAGE);
        int cLogTimestamp = logCursor.getColumnIndex(FIELD_LOG_TIMESTAMP);
        int cLogType = logCursor.getColumnIndex(FIELD_LOG_TYPE);

        Date logDate = getDateFromSqlite(logCursor.getString(cLogTimestamp));

        // If log datetime parsing fails we create a new Date object.
        if (logDate == null) {
            logDate = new Date();
        }

        return new HabitatLog(logCursor.getLong(cLogId),
                logDate,
                logCursor.getString(cLogTag),
                logCursor.getString(cLogMessage),
                logCursor.getInt(cLogType));
    }

    private static Date getDateFromSqlite(String dateTimeString) {
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return iso8601Format.parse(dateTimeString);
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
            return null;
        }
    }
}
