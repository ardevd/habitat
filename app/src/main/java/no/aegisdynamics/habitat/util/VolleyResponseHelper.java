package no.aegisdynamics.habitat.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class for Volley response handling
 */
public class VolleyResponseHelper {

    private VolleyResponseHelper() {
        // empty constructor
    }

    public static boolean hasResponseReturnedOK(JSONObject response) {
        try {
            int responseCode = response.getInt("code");
            return responseCode == 200;
        } catch (JSONException ex) {
            return false;
        }
    }
}
