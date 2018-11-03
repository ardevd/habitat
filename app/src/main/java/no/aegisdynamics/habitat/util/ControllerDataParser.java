package no.aegisdynamics.habitat.util;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import no.aegisdynamics.habitat.data.device.Controller;

/**
 * Helper class for parsing controller system info
 */
public class ControllerDataParser {

    private ControllerDataParser() {
        // Required empty constructor
    }

    private static final String[] supportedVersions = {"2.3.7","2.3.6", "2.3.8"};

    @Nullable
    public static Controller parseControllerJsonData(JSONObject object) {
        try {
            JSONObject dataObject = object.getJSONObject("data");
            int remoteId = dataObject.getInt("remote_id");
            String firmwareVersion = dataObject.getString("current_firmware");
            String firstStartUpDateString = dataObject.getString("first_start_up");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date firstStartUpDate = df.parse(firstStartUpDateString);
            String status = object.getString("message");
            return new Controller(remoteId, firmwareVersion, firstStartUpDate, status);
        } catch (JSONException | ParseException ex) {
            return null;
        }
    }

    public static boolean isControllerVersionSupported(String versionName) {
        return Arrays.asList(supportedVersions).contains(versionName);
    }
}
