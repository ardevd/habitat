package no.aegisdynamics.habitat.provider;


import android.net.Uri;
import android.provider.BaseColumns;

public interface DeviceDataContract extends BaseColumns {
    // Supported device types
    String DEVICE_TYPE_SWITCH_BINARY = "switchBinary";
    String DEVICE_TYPE_DOOR_LOCK = "doorlock";
    String DEVICE_TYPE_BATTERY = "battery";
    String DEVICE_TYPE_SWITCH_MULTILEVEL = "switchMultilevel";
    String DEVICE_TYPE_SENSOR_BINARY = "sensorBinary";
    String DEVICE_TYPE_SWITCH_CONTROL = "switchControl";
    String DEVICE_SENSOR_MULTILEVEL = "sensorMultilevel";
    String DEVICE_SENSOR_THERMOSTAT = "thermostat";
    String DEVICE_TYPE_TOGGLE_BUTTON = "toggleButton";

    String DEVICE_SPECIAL_CONTROLLER = "controller";

    // Provider Authority
    String AUTHORITY_AUTOMATIONS = "no.aegisdynamics.habitat.habitatdata.automation";
    String AUTHORITY_LOGS = "no.aegisdynamics.habitat.habitatdata.logs";


    // Provider ENTITIES
    String ENTITY_AUTOMATION = "automation";
    String ENTITY_LOG = "log";

    // Provider MIME Types
    String SINGLE_AUTOMATION_MIME_TYPE = "vnd.android.cursor.item/vnd.no.aegisdynamics.habitat.habitatdata.automation";
    String MULTIPLE_AUTOMATIONS_MIME_TYPE = "vnd.android.cursor.dir/vnd.no.aegisdynamics.habitat.habitatdata.automation";
    String SINGLE_LOG_MIME_TYPE = "vnd.android.cursor.item/vnd.no.aegisdynamics.habitat.habitatdata.log";
    String MULTIPLE_LOGS_MIME_TYPE = "vnd.android.cursor.dir/vnd.no.aegisdynamics.habitat.habitatdata.log";

    // Content URIs
    Uri CONTENT_URI_AUTOMATIONS = Uri.parse("content://" + AUTHORITY_AUTOMATIONS + "/" + ENTITY_AUTOMATION);
    Uri CONTENT_URI_LOGS = Uri.parse("content://" + AUTHORITY_LOGS + "/" + ENTITY_LOG);

    // Table names
    String TABLE_AUTOMATION = "automations";
    String TABLE_LOG = "logs";

    // ID Field
    String FIELD_ID = BaseColumns._ID;

    // Automation table fields
    String FIELD_AUTOMATION_NAME = "name";
    String FIELD_AUTOMATION_TYPE = "type";
    String FIELD_AUTOMATION_DESCRIPTION = "description";
    String FIELD_AUTOMATION_TRIGGER = "automation_trigger";
    String FIELD_AUTOMATION_COMMANDS = "commands";
    String FIELD_AUTOMATION_DEVICE = "device";

    // Automation types
    String AUTOMATION_TYPE_RECURRING ="recurring";
    String AUTOMATION_TYPE_SINGLE = "single";

    // Log table fields
    String FIELD_LOG_TAG = "tag";
    String FIELD_LOG_MESSAGE = "message";
    String FIELD_LOG_TIMESTAMP = "timestamp";
    String FIELD_LOG_TYPE = "type";
}
