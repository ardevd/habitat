package no.aegisdynamics.habitat.data.device;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class for parsing controller system info
 */
public class ControllerDataParser {

    public static Controller parseControllerJsonData(JSONObject object) {
        try {
            JSONObject dataObject = object.getJSONObject("data");
            int remoteId = dataObject.getInt("remote_id");
            String firmwareVersion = dataObject.getString("current_firmware");
            String firstStartUpDateString = dataObject.getString("first_start_up");
            //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date firstStartUpDate = df.parse(firstStartUpDateString);
            String status = object.getString("message");
            return new Controller(remoteId, firmwareVersion, firstStartUpDate, status);
        } catch (JSONException ex) {
            return null;
        } catch (ParseException ex) {
            return null;
        }
    }
}
