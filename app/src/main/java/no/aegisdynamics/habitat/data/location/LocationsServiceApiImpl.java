package no.aegisdynamics.habitat.data.location;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;

/**
 * Implementation of the Locations Service API that communicates with the ZAutomation API
 */

public class LocationsServiceApiImpl implements LocationsServiceApi {

    @Override
    public void getAllLocations(final Context context, final LocationsServiceCallback<List<Location>> callback) {
        String url = ZWayNetworkHelper.getZwayLocationsUrl(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<Location> locations = new ArrayList<>();
                        try {
                            // Verify response code
                            int responseCode = response.getInt("code");
                            if (responseCode == 200) {

                                JSONArray dataArray = response.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    try {
                                        JSONObject locationObject = dataArray.getJSONObject(i);
                                        Location location = parseLocationFromData(context, locationObject);
                                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                        if (!settings.getBoolean("hide_globalRoom", false) ||
                                                location.getId() != 0) {
                                            locations.add(location);
                                        }
                                    } catch (JSONException e) {
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        callback.onLoaded(locations);

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
    public void getLocation(final Context context, int locationId, final LocationsServiceCallback<Location> callback) {
        String url = ZWayNetworkHelper.getZwayLocationUrl(context, locationId);

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
                                        Location location = parseLocationFromData(context, dataObject);
                                        callback.onLoaded(location);
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

    @Override
    public void createLocation(final Context context, final Location location, final LocationsServiceCallback<Boolean> callback) {
        String url = ZWayNetworkHelper.getZwayLocationsUrl(context);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", location.getTitle());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Verify response code
                                int responseCode = response.getInt("code");
                                if (responseCode == 201) {
                                    callback.onLoaded(true);
                                } else {
                                    callback.onError(context.getString(R.string.error_generic));
                                }

                            } catch (JSONException e) {
                                callback.onError(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            if (error instanceof AuthFailureError) {
                                callback.onError(context.getString(R.string.devices_authentication_error));
                            } else {
                                if (error.getLocalizedMessage() != null) {
                                    callback.onError(error.getLocalizedMessage());
                                } else {
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
        } catch (JSONException ex){
            // Failed to add body object
        }
    }

    @Override
    public void updateLocation(final Context context, int locationId, String locationTitle, final LocationsServiceCallback<Boolean> callback) {
        String url = ZWayNetworkHelper.getZwayLocationUrl(context, locationId);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", locationTitle);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Verify response code
                                int responseCode = response.getInt("code");
                                if (responseCode == 200) {
                                    callback.onLoaded(true);
                                } else {
                                    callback.onError(context.getString(R.string.error_generic));
                                }

                            } catch (JSONException e) {
                                callback.onError(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            if (error instanceof AuthFailureError) {
                                callback.onError(context.getString(R.string.devices_authentication_error));
                            } else {
                                if (error.getLocalizedMessage() != null) {
                                    callback.onError(error.getLocalizedMessage());
                                } else {
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
        } catch (JSONException ex){
            // Failed to add body object
        }
    }

    @Override
    public void removeLocation(final Context context, final int locationId, final LocationsServiceCallback<Boolean> callback) {
        String url = ZWayNetworkHelper.getZwayLocationUrl(context, locationId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Verify response code
                                int responseCode = response.getInt("code");
                                if (responseCode == 201) {
                                    callback.onLoaded(true);
                                } else {
                                    callback.onError(context.getString(R.string.error_generic));
                                }

                            } catch (JSONException e) {
                                callback.onError(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            if (error instanceof AuthFailureError) {
                                callback.onError(context.getString(R.string.devices_authentication_error));

                            } // Volley cant handle empty 204 responses. So we need this hack for now.
                            else if (error instanceof ParseError) {
                                callback.onLoaded(true);
                            } else {
                                if (error.getLocalizedMessage() != null) {
                                    callback.onError(error.getLocalizedMessage());
                                } else {
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

    private int getDeviceCount(JSONArray namespaceArray) {
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

    private Location parseLocationFromData(Context context, JSONObject dataObject) throws JSONException {
        int locationId = dataObject.getInt("id");
        String locationTitle = dataObject.getString("title");
        String locationImage = dataObject.getString("default_img");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.getBoolean("use_user_images", true)) {
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
    }
}
