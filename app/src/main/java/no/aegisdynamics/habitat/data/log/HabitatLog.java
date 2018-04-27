package no.aegisdynamics.habitat.data.log;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Model class for an immutable log entry.
 */
public class HabitatLog implements Comparable<HabitatLog> {

    private final long id;

    private final Date timestamp;

    private final String tag;

    private final String message;

    private final int type;

    public HabitatLog(long id,
                      Date timestamp,
                      String tag,
                      String message,
                      int type) {

        this.id = id;
        this.timestamp = timestamp;
        this.tag = tag;
        this.message = message;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }


    @Override
    public int compareTo(@NonNull HabitatLog habitatLog) {
        if (timestamp.before(habitatLog.getTimestamp())) {
            return -1;
        } else if (timestamp.after(habitatLog.getTimestamp())) {
            return 1;
        } else {
            return 0;
        }
    }
}
