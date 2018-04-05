package no.aegisdynamics.habitat.data.profile;

import android.content.Context;

import com.android.volley.AuthFailureError;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Implementation of the Profile service API. Communicates with the Z-way REST API.
 */

public class ProfileServiceApiImpl implements ProfileServiceApi {

    @Override
    public void getProfile(final Context context, final String username, final ProfileServiceCallback<Profile> callback) {
        String url = ZWayNetworkHelper.getZwayProfileUrl(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verify response code
                            int responseCode = response.getInt("code");
                            if (responseCode == 200) {

                                JSONArray dataArray = response.getJSONArray("data");
                                Pattern pattern = Pattern.compile("(\\D+)");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    try {
                                        JSONObject profileObject = dataArray.getJSONObject(i);
                                        // Check if this is the correct profile for the given username.

                                        Matcher matcher = pattern.matcher(profileObject.getString("qrcode"));
                                        if (matcher.find()) {

                                            if (matcher.group(0).equals(username)) {
                                                // Parse user profile data
                                                String name = profileObject.getString("name");
                                                String email = profileObject.getString("email");
                                                int userId = profileObject.getInt("id");
                                                JSONArray dashboardDevices = profileObject.getJSONArray("dashboard");
                                                List<String> dashboardDevicesList = new ArrayList();
                                                for (int x = 0; x < dashboardDevices.length(); x++) {
                                                    dashboardDevicesList.add(dashboardDevices.getString(x));
                                                }

                                                callback.onLoaded(new Profile(userId, name, email, dashboardDevicesList));
                                                return;
                                            }
                                        }

                                    } catch (JSONException e) {
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        callback.onError("No profile found");

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
}
