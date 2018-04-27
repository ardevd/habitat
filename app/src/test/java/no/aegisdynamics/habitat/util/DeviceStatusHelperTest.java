package no.aegisdynamics.habitat.util;

import org.junit.Test;

import no.aegisdynamics.habitat.provider.DeviceDataContract;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the DeviceStatusHelper class.
 */

public class DeviceStatusHelperTest {

    @Test
    public void parseStatusMessageToBoolean_tests() {
        assertEquals(DeviceStatusHelper.parseStatusMessageToBoolean("on"), true);
        assertEquals(DeviceStatusHelper.parseStatusMessageToBoolean("off"), false);
        assertEquals(DeviceStatusHelper.parseStatusMessageToBoolean("open"), true);
        assertEquals(DeviceStatusHelper.parseStatusMessageToBoolean("close"), false);
    }

    @Test
    public void getThermostatCommand_tests() {
        assertEquals(DeviceStatusHelper.getThermostatCommand(30.0), "exact?level=30.00");
    }

    @Test
    public void getSwitchMultilevelExactCommand_test () {
        assertEquals(DeviceStatusHelper.getSwitchMultilevelExactCommand(10), "exact?level=10");
    }

    @Test
    public void parseStatusToIconSuffix_tests() {
        assertEquals(DeviceStatusHelper.parseStatusToIconSuffix(DeviceDataContract.DEVICE_TYPE_SWITCH_BINARY,
                "on"), "-on");
        assertEquals(DeviceStatusHelper.parseStatusToIconSuffix(DeviceDataContract.DEVICE_TYPE_DOOR_LOCK,
                "open"), "-open");
        assertEquals(DeviceStatusHelper.parseStatusToIconSuffix(DeviceDataContract.DEVICE_TYPE_DOOR_LOCK,
                "close"), "-closed");
        assertEquals(DeviceStatusHelper.parseStatusToIconSuffix(DeviceDataContract.DEVICE_TYPE_SWITCH_MULTILEVEL,
                "full"), "-full");
        assertEquals(DeviceStatusHelper.parseStatusToIconSuffix(DeviceDataContract.DEVICE_TYPE_SENSOR_BINARY,
                "on"), "-on");
    }

    @Test
    public void parseBooleanStatusToCommandTest() {
        assertEquals(DeviceStatusHelper.parseBooleanStatusToCommand(
                DeviceDataContract.DEVICE_TYPE_DOOR_LOCK, true),
                "open");
        assertEquals(DeviceStatusHelper.parseBooleanStatusToCommand(
                DeviceDataContract.DEVICE_TYPE_DOOR_LOCK, false),
                "close");
        assertEquals(DeviceStatusHelper.parseBooleanStatusToCommand(
                DeviceDataContract.DEVICE_TYPE_SWITCH_BINARY, true),
                "on");
        assertEquals(DeviceStatusHelper.parseBooleanStatusToCommand(
                DeviceDataContract.DEVICE_TYPE_SWITCH_BINARY, false),
                "off");
        assertEquals(DeviceStatusHelper.parseBooleanStatusToCommand(
                DeviceDataContract.DEVICE_TYPE_SWITCH_MULTILEVEL, true),
                "on");
        assertEquals(DeviceStatusHelper.parseBooleanStatusToCommand(
                DeviceDataContract.DEVICE_TYPE_SWITCH_MULTILEVEL, false),
                "off");
    }
}
