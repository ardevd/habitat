package no.aegisdynamics.habitat.data.profile;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import no.aegisdynamics.habitat.util.ErrorParserHelper;
import no.aegisdynamics.habitat.util.LogHelper;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;
import no.aegisdynamics.habitat.util.VolleyResponseHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Implementation of the Profile service API. Communicates with the Z-way REST API.
 */

public class ProfileServiceApiImpl implements ProfileServiceApi {

    private static final String TAG = "Profile API";

    @Override
    public void getProfile(final Context context, final String username, final ProfileServiceCallback<Profile> callback) {
        String url = ZWayNetworkHelper.getZwayProfileUrl(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (VolleyResponseHelper.hasResponseReturnedOK(response)) {

                                JSONObject dataObject = response.getJSONObject("data");
                                Profile userProfile = ProfileDataHelper.getProfileFromJsonData(dataObject);
                                if (userProfile != null) {
                                    callback.onLoaded(userProfile);
                                    return;
                                }
                            }

                        } catch (JSONException e) {
                            LogHelper.logError(context, TAG, e.getMessage());
                        }

                        callback.onError("No profile data found");

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogHelper.logError(context, TAG, error.getMessage());
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
