package no.aegisdynamics.habitat.data.profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for parsing profile data from JSON API.
 */
public class ProfileDataHelper {

    private ProfileDataHelper() {
        // Empty constructor
    }

    /**
     * Parse profile data from JSONObject.
     * @param dataObject JSONObject containing profile data from Z-Way API
     * @return valid user Profile object.
     */
    public static Profile getProfileFromJsonData(JSONObject dataObject) {

        try {
            int profileId = dataObject.getInt("id");
            String profileUserName = dataObject.getString("login");
            String profileEmail = dataObject.getString("email");
            String profileName = dataObject.getString("name");

            // parse dashboard devices
            JSONArray dashboardDevices = dataObject.getJSONArray("dashboard");
            List<String> dashboardDevicesList = new ArrayList<>();
            for (int i = 0; i < dashboardDevices.length(); i++) {
                dashboardDevicesList.add(dashboardDevices.getString(i));
            }

            return new Profile(profileId, profileUserName, profileName, profileEmail,
                    dashboardDevicesList);
        } catch (JSONException ex) {
            return null;
        }


    }
}
