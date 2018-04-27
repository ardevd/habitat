package no.aegisdynamics.habitat.util;

import android.content.Context;

import java.util.Date;

import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.log.HabitatLog;
import no.aegisdynamics.habitat.data.log.LogConstants;
import no.aegisdynamics.habitat.data.log.LogRepository;

/**
 * Helper class for logging messages to the custom log engine
 */
public class LogHelper {

    private LogHelper() {
        // Empty private constructor
    }

    public static void logError(Context context, String tag, String message) {
        HabitatLog logEntry = new HabitatLog(0, new Date(),
                tag, message, LogConstants.LOG_TYPE_ERROR);

        logMessage(context, logEntry);
    }

    public static void logDebug(Context context, String tag, String message) {
        HabitatLog logEntry = new HabitatLog(0, new Date(),
                tag, message, LogConstants.LOG_TYPE_DEBUG);

        logMessage(context, logEntry);
    }

    public static void logInfo(Context context, String tag, String message) {
        HabitatLog logEntry = new HabitatLog(0, new Date(),
                tag, message, LogConstants.LOG_TYPE_INFO);

        logMessage(context, logEntry);
    }

    private static void logMessage(Context context, HabitatLog logEntry) {
        LogRepository repository = Injection.provideLogRepository(context);
        repository.createLog(logEntry, new LogRepository.CreateLogCallback() {
            @Override
            public void onLogCreated() {
                // Ignore callback
            }

            @Override
            public void onLogCreatedError(String error) {
                // Ignore callback for now
            }
        });
    }
}
