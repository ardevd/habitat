package no.aegisdynamics.habitat.data.module;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.util.GsonRequest;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;
import no.aegisdynamics.habitat.util.VolleyResponseHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;

/**
 * Implementation of the Module Service API. Loads and interacts with Z-Way modules
 * through the Z-Way API.
 */
public class ModuleServiceApiImpl implements ModuleServiceApi {


    @Override
    public void getAllModules(@NonNull Context context, final ModuleServiceCallback<List<Module>> callback) {
        String url = ZWayNetworkHelper.getZwayModulesUrl(context);
        Map<String, String> authHeader = ZWayNetworkHelper.getAuthenticationHeaders(context);
        GsonRequest gsonRequest = new GsonRequest(url, ModuleListResponse.class, authHeader, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                ModuleListResponse modules = (ModuleListResponse) response;
                callback.onSuccess(modules.modules);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.getMessage());
            }
        });

        RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(gsonRequest);
    }

    @Override
    public void getModule(Context context, ModuleServiceCallback<Module> callback) {

    }

    @Override
    public void installModule(final Context context, String url, final ModuleServiceCallback callback) {
        try {
            String moduleInstallUrl = ZWayNetworkHelper
                    .getZwayModuleInstallurl(context, URLEncoder.encode(url, "UTF-8"));

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.POST, moduleInstallUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (VolleyResponseHelper.hasResponseReturnedOK(response)) {
                                callback.onSuccess(200);
                            } else {
                                callback.onError("Error");
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
                public Map<String, String> getHeaders() {
                    return ZWayNetworkHelper.getAuthenticationHeaders(context);
                }
            };

            // Access the RequestQueue through your singleton class.
            RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);
        } catch (UnsupportedEncodingException ex) {
            callback.onError("error");
        }
    }

    /**
     * The Z-way module delete API currently does not work and just returns a 404.
     */
    @Override
    public void deleteModule(final Context context, Module module, final ModuleServiceCallback callback) {
        String url = ZWayNetworkHelper.getZwayModuleDeleteUrl(context, module.getId());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (VolleyResponseHelper.hasResponseReturnedOK(response)) {
                            callback.onSuccess(true);
                        } else {
                            callback.onError(context.getString(R.string.error_generic));
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
            public Map<String, String> getHeaders() {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);

    }
}
