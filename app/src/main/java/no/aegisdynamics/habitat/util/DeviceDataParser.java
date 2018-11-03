package no.aegisdynamics.habitat.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.aegisdynamics.habitat.data.device.Device;

public class DeviceDataParser {

    private DeviceDataParser() {
        // Required empty constructor
    }

    public static String[] parseTags(JSONArray tagsArray){
        String [] tags;
        List<String> tagsList = new ArrayList<>();
        try {
            for (int i = 0; i < tagsArray.length(); i++) {
                tagsList.add(tagsArray.getString(i));
            }
            tags = tagsList.toArray(new String[tagsList.size()]);
            return tags;
        } catch (JSONException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static Device parseDeviceData(JSONObject dataObject, Map locationMap) throws  JSONException {
        String deviceId = dataObject.getString("id");
        String deviceType = dataObject.getString("deviceType");
        int creatorId = dataObject.getInt("creatorId");
        int deviceLocationId = dataObject.getInt("location");
        String deviceLocationTitle = locationMap.get(deviceLocationId).toString();
        String[] deviceTags = parseTags(dataObject.getJSONArray("tags"));

        // Metrics Object
        JSONObject deviceMetrics = dataObject.getJSONObject("metrics");
        String deviceTitle = deviceMetrics.getString("title");
        String deviceStatus = deviceMetrics.getString("level");
        String deviceStatusNotation = null;
        try {
            deviceStatusNotation = deviceMetrics.getString("scaleTitle");
        } catch (JSONException e) {
            // No scale title available
        }
        String deviceProbeTitle = null;
        try {
            deviceProbeTitle = deviceMetrics.getString("probeTitle");
        } catch (JSONException e) {
            // No scale title available
        }

        int deviceMinValue = 0;
        int deviceMaxValue = 0;
        try {
            deviceMinValue = deviceMetrics.getInt("min");
            deviceMaxValue = deviceMetrics.getInt("max");
        } catch (JSONException e) {
            Log.d("DeviceParser", "No min/max values for device");
        }

        String deviceIconName = null;
        try {
            String deviceIconNameString = deviceMetrics.getString("icon");
            if (deviceIconNameString.length() > 0) {
                deviceIconName = deviceIconNameString;
            }

        } catch (JSONException e) {
            // No deviceIcon name available.
        }

        return new Device(deviceId, deviceTitle, deviceType,
                deviceLocationTitle, deviceLocationId, creatorId, deviceTags, deviceStatus,
                deviceMinValue, deviceMaxValue,
                deviceStatusNotation, deviceProbeTitle, deviceIconName);

    }
}
