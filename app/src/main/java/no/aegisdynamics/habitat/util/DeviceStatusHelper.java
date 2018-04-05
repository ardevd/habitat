package no.aegisdynamics.habitat.util;

import java.util.Locale;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Device specific helper functions
 */
public class DeviceStatusHelper implements DeviceDataContract {
    private final static String SWITCH_BINARY_ON = "on";
    private final static String SWITCH_BINARY_OFF = "off";
    private final static String DOOR_LOCK_OPEN = "open";
    private final static String DOOR_LOCK_CLOSE = "close";
    private final static String SWITCH_MULTILEVEL_ON = "on";
    private final static String SWITCH_MULTILEVEL_OFF = "off";


    public static boolean parseStatusMessageToBoolean(String status) {
        return status.equals("on") || status.equals("open");
    }

    public static String getThermostatCommand(double temperature) {
        return String.format(Locale.getDefault(),"exact?level=%.2f", temperature);
    }

    public static String getSwitchMultilevelExactCommand(int value) {
        return String.format(Locale.getDefault(),"exact?level=%d", value);
    }

    public static String parseStatusToIconSuffix(String deviceType, String deviceState) {
        if (deviceType != null) {
            switch (deviceType) {
                case DEVICE_TYPE_SWITCH_BINARY:
                    return String.format("-%s", deviceState);
                case DEVICE_TYPE_DOOR_LOCK:
                    if (deviceState.equals(DOOR_LOCK_CLOSE)) {
                        return String.format("-%sd", deviceState);
                    } else {
                        return String.format("-%s", deviceState);
                    }
                case DEVICE_TYPE_SWITCH_MULTILEVEL:
                    return String.format("-%s", deviceState);
                case DEVICE_TYPE_SENSOR_BINARY:
                    return String.format("-%s", deviceState);
                default:
                    // If device type is not one of the above we either dont support it or no
                    // device icon name suffix is needed.
                    throw new IllegalArgumentException("Unsupported device type or not status suffix required: "
                            + deviceType);
            }
        } else {
            throw new NullPointerException("Device type is null");
        }
    }

    public static String parseBooleanStatusToCommand(String deviceType, boolean commandedState) {
        if (deviceType != null) {
            switch (deviceType) {
                case DEVICE_TYPE_SWITCH_BINARY:
                    if (commandedState) {
                        return SWITCH_BINARY_ON;
                    } else {
                        return SWITCH_BINARY_OFF;
                    }
                case DEVICE_TYPE_DOOR_LOCK:
                    if (commandedState) {
                        return DOOR_LOCK_OPEN;
                    } else {
                        return DOOR_LOCK_CLOSE;
                    }
                case DEVICE_TYPE_SWITCH_MULTILEVEL:
                    if (commandedState) {
                        return SWITCH_MULTILEVEL_ON;
                    } else {
                        return SWITCH_MULTILEVEL_OFF;
                    }
                default:
                    throw new IllegalArgumentException("Unsupported device type: " + deviceType);

            }
        } else {
            throw new NullPointerException("Device type is null");
        }
    }

    /**
     *
     * @param device to be commanded
     * @param commandedState the desired state the command should achieve
     * @return valid command value if supported
     */
    public static String parseBooleanStatusFromDeviceToCommand(Device device, boolean commandedState) {
        String deviceType = device.getType();
        return parseBooleanStatusToCommand(deviceType, commandedState);
    }
}
