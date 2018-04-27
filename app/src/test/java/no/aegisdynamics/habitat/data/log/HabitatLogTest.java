package no.aegisdynamics.habitat.data.log;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the HabitatLog class
 */
public class HabitatLogTest {
    private static final Date LOG_DATE = new Date();
    private static final String LOG_TAG = "TAG";
    private static final String LOG_MESSAGE = "Log message";
    private static final int LOG_TYPE = 1;
    private static final int LOG_ID = 1;

    private static final HabitatLog LOG = new HabitatLog(LOG_ID, LOG_DATE, LOG_TAG, LOG_MESSAGE, LOG_TYPE);

    @Test
    public void testGetLogDate() {
        assertEquals(LOG_DATE, LOG.getTimestamp());
    }

    @Test
    public void testGetLogId() {
        assertEquals(LOG_ID, LOG.getId());
    }

    @Test
    public void testGetLogTag() {
        assertEquals(LOG_TAG, LOG.getTag());
    }

    @Test
    public void testGetLogMessage() {
        assertEquals(LOG_MESSAGE, LOG.getMessage());
    }

    @Test
    public void testGetLogType() {
        assertEquals(LOG_TYPE, LOG.getType());
    }

    @Test
    public void testCompareLogEntries() {
        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.DAY_OF_MONTH, -5);
        HabitatLog NEW_LOG = new HabitatLog(LOG_ID, calendar.getTime(), LOG_TAG, LOG_MESSAGE, LOG_TYPE);
        assertEquals(-1, NEW_LOG.compareTo(LOG));
        assertEquals(1, LOG.compareTo(NEW_LOG));
    }


}
