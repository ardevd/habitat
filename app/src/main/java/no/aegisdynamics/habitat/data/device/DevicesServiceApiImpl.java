package no.aegisdynamics.habitat.data.device;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.provider.DeviceDataContract;
import no.aegisdynamics.habitat.util.LogHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;

/**
 * Implementation of the Devices Service API that communicates with the ZAutomation API
 */
public class DevicesServiceApiImpl implements DevicesServiceApi, DeviceDataContract {

    private final String TAG = "DevicesServiceApiImpl";

    @Override
    public void getAllDevices(final Context context, final DevicesServiceCallback callback) {
        /*
         * We first need to retrieve locations so that we can map it to the location id for each device.
         * Then we load all devices
         */
       getLocationTitles(context, callback, null);
    }

    /**
     *
     * @param context - Valid context
     * @param deviceId - device id string of requested device
     * @param callback - service callback
     */
    @Override
    public void getDevice(Context context, String deviceId, DevicesServiceCallback<Device> callback) {

        getLocationTitles(context, callback, deviceId);
    }

    private void getDeviceImpl(final Context context, final String deviceId, final DevicesServiceCallback<Device> callback, final HashMap locationMap) {
        String url = ZWayNetworkHelper.getZwayDeviceUrl(context, deviceId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verify response code
                            int responseCode = response.getInt("code");
                            if (responseCode == 200) {

                                JSONObject dataObject = response.getJSONObject("data");
                                try {
                                    Device device = parseDeviceData(dataObject, locationMap);
                                    callback.onLoaded(device);
                                } catch (JSONException e) {
                                    callback.onError(context.getString(R.string.error_json));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(context.getString(R.string.error_json));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error));
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);

    }

    private void getAllDevicesImpl(final Context context, final DevicesServiceCallback callback, final HashMap locationMap) {
        String url = ZWayNetworkHelper.getZwayDevicesUrl(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            CreateDevicesTask task = new CreateDevicesTask(context,
                                    callback, locationMap);

                            task.execute(response);
                        } else {
                            callback.onError("No data received from server");
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error));
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private void getLocationTitles(final Context context, final DevicesServiceCallback callback, @Nullable final String deviceId) {
        String url = ZWayNetworkHelper.getZwayLocationsUrl(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verify response code
                            int responseCode = response.getInt("code");
                            if (responseCode == 200) {

                                JSONArray dataArray = response.getJSONArray("data");
                                // Create a hash map
                                HashMap locationsMap = new HashMap();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    try {
                                        JSONObject locationObject = dataArray.getJSONObject(i);
                                        int locationId = locationObject.getInt("id");
                                        String locationTitle = locationObject.getString("title");
                                        locationsMap.put(locationId, locationTitle);

                                    } catch (JSONException e) {
                                        Log.d(TAG, "Unable to parse location data");
                                    }
                                }
                                // We have all locations, now lets get either all devices or a single device.
                                if (deviceId != null) {
                                    getDeviceImpl(context, deviceId, callback, locationsMap);
                                } else {
                                    getAllDevicesImpl(context, callback, locationsMap);
                                }
                            }

                        } catch (JSONException error) {
                            error.printStackTrace();
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error));
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void sendCommand(final Context context, String deviceId, String command, final DevicesServiceCallback<Boolean> callback) {
        String url;
        if (deviceId.equals(DEVICE_SPECIAL_CONTROLLER)) {
            url = ZWayNetworkHelper.getZwayControllerRestartUrl(context);
        } else {
            url = String.format("%s/%s/command/%s", ZWayNetworkHelper.getZwayDevicesUrl(context), deviceId, command);
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("code") == 200) {
                                callback.onLoaded(true);
                            } else {
                                callback.onError(context.getString(R.string.error_generic));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error));
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Restarting controller takes about 10 seconds. Set timeout value accordingly.
        RetryPolicy policy = new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void sendAutomatedCommand(final Context context, String deviceId, String command, final String automationTitle, final AutomatedCommandServiceCallback<Boolean> callback) {
        String url = String.format("%s/%s/command/%s", ZWayNetworkHelper.getZwayDevicesUrl(context), deviceId, command);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("code") == 200) {
                                callback.onSuccess(automationTitle);
                            } else {
                                callback.onError(context.getString(R.string.error_generic), automationTitle);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(e.getLocalizedMessage(), automationTitle);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error), automationTitle);
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage(), automationTitle);
                            } else{
                                callback.onError(context.getString(R.string.error_generic), automationTitle);
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void getControllerStatus(final Context context, final DevicesServiceCallback<Boolean> callback) {
        String url = ZWayNetworkHelper.getZwayControllerStatusUrl(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verify response code
                            int responseCode = response.getInt("code");
                            if (responseCode == 200) {
                                // Controller is up
                                callback.onLoaded(true);
                            } else {
                                callback.onError(String.format("%s: %d", R.string.devices_controller_not_available, responseCode));
                            }

                        } catch (JSONException error) {
                            error.printStackTrace();
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error));
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void registerFCMDeviceToken(final Context context, String deviceToken,
                                       final FCMDeviceTokenRegistrationServiceCallback callback) {
        String deviceIdentifier = String.format("%s-%s", Build.MODEL, deviceToken.substring(0,4));
        String url = ZWayNetworkHelper.getZwayHabitatAppRegisterTokenUrl(context, deviceToken, deviceIdentifier);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verify response code
                            int responseCode = response.getInt("code");
                            if (responseCode == 200) {
                                // Device token was registered
                                callback.onSuccess();
                            } else {
                                callback.onError(String.format("%s: %d", R.string.devices_controller_not_available, responseCode));
                            }

                        } catch (JSONException error) {
                            error.printStackTrace();
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error));
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void getControllerData(final Context context, final DevicesServiceCallback<Controller> callback) {
        String url = ZWayNetworkHelper.getZwayControllerDataUrl(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verify response code
                            int responseCode = response.getInt("code");
                            if (responseCode == 200) {
                                // Controller is up
                                Controller controller = ControllerDataParser.parseControllerJsonData(response);
                                if (controller != null) {
                                    callback.onLoaded(controller);
                                } else {
                                    callback.onError(context.getString(R.string.error_controller_data_error));
                                }
                            } else {
                                callback.onError(String.format("%s: %d", R.string.devices_controller_not_available, responseCode));
                            }

                        } catch (JSONException error) {
                            error.printStackTrace();
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof AuthFailureError) {
                            callback.onError(context.getString(R.string.devices_authentication_error));
                        }
                        else {
                            if (error.getLocalizedMessage() != null) {
                                callback.onError(error.getLocalizedMessage());
                            } else{
                                callback.onError(context.getString(R.string.error_generic));
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private String[] parseTags(JSONArray tagsArray){
        String [] tags;
        List<String> tagsList = new ArrayList<>();
        try {
            for(int i = 0; i < tagsArray.length(); i++){
                    tagsList.add(tagsArray.getString(i));
                }
            tags = tagsList.toArray( new String[tagsList.size()] );
            return tags;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Device parseDeviceData(JSONObject dataObject, HashMap locationMap) throws  JSONException {
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
            Log.d(TAG, "No min/max values for device");
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

    private class CreateDevicesTask extends AsyncTask<JSONObject, Void, Void> {

        private final Context mContext;
        private final DevicesServiceCallback mCallback;
        private final HashMap mLocationMap;
        private final List<Device> devices;

        private CreateDevicesTask (Context context, DevicesServiceCallback callback, final HashMap locationMap) {
            mContext = context;
            mCallback = callback;
            mLocationMap = locationMap;
            devices = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            JSONObject response = jsonObjects[0];
            try {
                // Verify response code
                int responseCode = response.getInt("code");
                if (responseCode == 200) {

                    JSONObject dataObjects = response.getJSONObject("data");
                    JSONArray deviceArray = dataObjects.getJSONArray("devices");

                    for (int i = 0; i < deviceArray.length(); i++) {
                        try {
                            JSONObject dataObject = deviceArray.getJSONObject(i);
                            // Only add device if its marked as visible and not hidden
                            boolean visibility = dataObject.getBoolean("visibility");
                            boolean hidden = dataObject.getBoolean("permanently_hidden");
                            if (visibility && !hidden) {
                                devices.add(parseDeviceData(dataObject, mLocationMap));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (JSONException e) {
                LogHelper.logError(mContext, TAG, e.getMessage());
            }
            // Iterate over devices and remove globalRoom devices if set to do so
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (settings.getBoolean("hide_globalRoom", false)) {
                List<Device> globalRoomDevices = new ArrayList<>();
                for (Device device : devices) {
                    if (device.getLocationId() == 0) {
                        globalRoomDevices.add(device);
                    }
                }
                devices.removeAll(globalRoomDevices);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCallback.onLoaded(devices);
        }


    }
}
