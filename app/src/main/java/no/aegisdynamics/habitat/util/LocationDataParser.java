package no.aegisdynamics.habitat.util;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.aegisdynamics.habitat.data.location.Location;

public class LocationDataParser {

    private LocationDataParser() {
        // Required empty constructor
    }

    @Nullable
    public static Location parseLocationFromData(JSONObject dataObject, boolean useUserImages) {
        try {
            int locationId = dataObject.getInt("id");
            String locationTitle = dataObject.getString("title");
            String locationImage = dataObject.getString("default_img");
            if (useUserImages) {
                locationImage = dataObject.getString("user_img");
            }
            int locationDeviceCount;
            if (locationImage.length() == 0) {
                locationImage = null;
            }
            try {
                JSONArray namespacesArray = dataObject.getJSONArray("namespaces");
                locationDeviceCount = getDeviceCount(namespacesArray);
            } catch (JSONException ex) {
                locationDeviceCount = 0;
            }
            return new Location(locationId, locationTitle, locationImage, locationDeviceCount);
        } catch (JSONException ex) {
            return null;
        }
    }

    private static int getDeviceCount(JSONArray namespaceArray) {
        try {
            for (int i = 0; i < namespaceArray.length(); i++) {
                JSONObject namespaceObject = namespaceArray.getJSONObject(i);
                String namespaceId = namespaceObject.getString("id");
                if (namespaceId.equals("devices_all")) {
                    // Enumerate devices and get count
                    JSONArray deviceArray = namespaceObject.getJSONArray("params");
                    return deviceArray.length();
                }
            }
        } catch (JSONException ex) {
            return 0;
        }
        return 0;
    }
}
