package no.aegisdynamics.habitat.data.device;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the device model class.
 */

public class DeviceTest {

    private static final String DEVICE_ID = "device_1";
    private static final String DEVICE_TITLE = "Device 1";
    private static final String DEVICE_TYPE = "device";
    private static final String DEVICE_LOCATION = "room";
    private static final int DEVICE_LOCATION_ID = 1;
    private static final int DEVICE_CREATOR_ID = 2;
    private static final String[] DEVICE_TAGS = new String[]{"Tag 1", "Tag2"};
    private static final String DEVICE_STATUS = "on";
    private static final int DEVICE_MIN_VALUE = 0;
    private static final int DEVICE_MAX_VALUE = 100;
    private static final String DEVICE_STATUS_NOTATION = "celsius";
    private static final String DEVICE_PROBE_TITLE = "Temperature";
    private static final String DEVICE_ICON_NAME = "deviceicon";

    private static final Device DEVICE = new Device(DEVICE_ID, DEVICE_TITLE, DEVICE_TYPE, DEVICE_LOCATION,
            DEVICE_LOCATION_ID, DEVICE_CREATOR_ID, DEVICE_TAGS, DEVICE_STATUS, DEVICE_MIN_VALUE,
            DEVICE_MAX_VALUE, DEVICE_STATUS_NOTATION, DEVICE_PROBE_TITLE, DEVICE_ICON_NAME);

    @Test
    public void testGetDeviceId() {
        assertEquals(DEVICE_ID, DEVICE.getId());
    }

    @Test
    public void testGetDeviceTitle() {
        assertEquals(DEVICE_TITLE, DEVICE.getTitle());
    }

    @Test
    public void testGetDeviceType() {
        assertEquals(DEVICE_TYPE, DEVICE.getType());
    }

    @Test
    public void testGetDeviceLocation() {
        assertEquals(DEVICE_LOCATION, DEVICE.getLocation());
    }

    @Test
    public void testGetDeviceLocationId() {
        assertEquals(DEVICE_LOCATION_ID, DEVICE.getLocationId());
    }

    @Test
    public void testGetDeviceCreatorId() {
        assertEquals(DEVICE_CREATOR_ID, DEVICE.getCreatorId());
    }

    @Test
    public void testGetDeviceTags() {
        assertEquals(DEVICE_TAGS, DEVICE.getTags());
    }

    @Test
    public void testGetDeviceStatus() {
        assertEquals(DEVICE_STATUS, DEVICE.getStatus());
    }

    @Test
    public void testGetDeviceMinValue() {
        assertEquals(DEVICE_MIN_VALUE, DEVICE.getDeviceMinValue());
    }

    @Test
    public void testGetDeviceMaxValue() {
        assertEquals(DEVICE_MAX_VALUE, DEVICE.getDeviceMaxValue());
    }

    @Test
    public void testGetDeviceStatusNotation() {
        assertEquals(DEVICE_STATUS_NOTATION, DEVICE.getStatusNotation());
    }

    @Test
    public void testGetDeviceProbeTitle() {
        assertEquals(DEVICE_PROBE_TITLE, DEVICE.getDeviceProbeTitle());
    }

    @Test
    public void testGetDeviceIconName() {
        assertEquals(DEVICE_ICON_NAME, DEVICE.getIconName());
    }

    @Test
    public void testDeviceHasIcon() {
        assertEquals(true, DEVICE.hasIcon());
    }

    @Test
    public void testDeviceIsNotEmpty() {
        assertEquals(false, DEVICE.isEmpty());
    }

    @Test
    public void testDeviceToString() {
        assertEquals(DEVICE_TITLE, DEVICE.toString());
    }

    @Test
    public void testDeviceIsNotSwitchable() {
        assertEquals(false, DEVICE.isSwitchable());
    }
}
