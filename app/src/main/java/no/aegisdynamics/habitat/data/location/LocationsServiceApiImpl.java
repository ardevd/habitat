package no.aegisdynamics.habitat.data.location;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
import no.aegisdynamics.habitat.util.ErrorParserHelper;
import no.aegisdynamics.habitat.util.LocationDataParser;
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

                                    JSONObject locationObject = dataArray.getJSONObject(i);

                                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                    Location location = LocationDataParser.parseLocationFromData(locationObject,
                                            settings.getBoolean("use_user_images", true));
                                    if (location != null && (!settings.getBoolean("hide_globalRoom", false) ||
                                            location.getId() != 0)) {
                                        locations.add(location);
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
                        callback.onError(ErrorParserHelper.parseErrorToErrorMessage(context, error));
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);

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
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                Location location = LocationDataParser.parseLocationFromData(dataObject,
                                        settings.getBoolean("use_user_images", true));
                                if (location != null) {
                                    callback.onLoaded(location);
                                } else {
                                    callback.onError(context.getString(R.string.error_json));
                                }
                            }

                        } catch (JSONException e) {
                            callback.onError(context.getString(R.string.error_json));
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(ErrorParserHelper.parseErrorToErrorMessage(context, error));
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);

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
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onError(ErrorParserHelper.parseErrorToErrorMessage(context, error));
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    return ZWayNetworkHelper.getAuthenticationHeaders(context);
                }

            };

            // Access the RequestQueue through your singleton class.
            RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);
        } catch (JSONException ex) {
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
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onError(ErrorParserHelper.parseErrorToErrorMessage(context, error));
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    return ZWayNetworkHelper.getAuthenticationHeaders(context);
                }

            };

            // Access the RequestQueue through your singleton class.
            RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);
        } catch (JSONException ex) {
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
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(ErrorParserHelper.parseErrorToErrorMessage(context, error));
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }

        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);
    }
}
