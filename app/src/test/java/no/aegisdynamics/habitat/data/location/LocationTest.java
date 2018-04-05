package no.aegisdynamics.habitat.data.location;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the location class
 */

public class LocationTest {

    private static final int LOCATION_ID = 1;
    private static final String LOCATION_TITLE = "Office";
    private static final String LOCATION_IMAGE_NAME ="office.png";
    private static final int LOCATION_DEVICE_COUNT = 9;

    private static final Location LOCATION = new Location(LOCATION_ID, LOCATION_TITLE,
            LOCATION_IMAGE_NAME, LOCATION_DEVICE_COUNT);

    @Test
    public void testGetLocationId() throws Exception {
        assertEquals(LOCATION_ID, LOCATION.getId());
    }

    @Test
    public void testGetLocationTitle() throws Exception {
        assertEquals(LOCATION_TITLE, LOCATION.getTitle());
    }

    @Test
    public void testGetLocationImageName() throws Exception {
        assertEquals(LOCATION_IMAGE_NAME, LOCATION.getImageName());
    }

    @Test
    public void testGetLocationDeviceCount() throws Exception {
        assertEquals(LOCATION_DEVICE_COUNT, LOCATION.getDeviceCount());
    }

}
