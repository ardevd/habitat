package no.aegisdynamics.habitat.data.notifications;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.util.ErrorParserHelper;
import no.aegisdynamics.habitat.util.LogHelper;
import no.aegisdynamics.habitat.util.NotificationTimestampComparator;
import no.aegisdynamics.habitat.util.VolleyResponseHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;

/**
 * Implementation of the Notification Service API that communicates with the ZAutomation API
 */
public class NotificationsServiceApiImpl implements NotificationsServiceApi {

    private static final String TAG = "NotificationsServiceApi";

    @Override
    public void getAllNotifications(Context context, final NotificationsServiceCallback<List<Notification>> callback) {
        getNotifications(context, null, callback);
    }

    @Override
    public void getNotificationsForDevice(Context context, String deviceId, final NotificationsServiceCallback<List<Notification>> callback) {
        getNotifications(context, deviceId, callback);
    }

    @Override
    public void updateNotification(final Context context, final long notificationId, final NotificationsServiceCallback<Boolean> callback) {
        String url = ZWayNetworkHelper.getZwayNotificationUpdateUrl(context, notificationId);
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", notificationId);
            obj.put("redeemed", true);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, url, obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // Verify response code
                            if (VolleyResponseHelper.hasResponseReturnedOK(response)) {
                                callback.onLoaded(true);
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
                            }
                            // Volley cant handle empty 204 responses. So we need this hack for now.
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
                public Map<String, String> getHeaders() {
                    return ZWayNetworkHelper.getAuthenticationHeaders(context);
                }
            };

            // Access the RequestQueue through your singleton class.
            RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);
        } catch (JSONException ex) {
            callback.onError(ex.getMessage());
        }
    }

    @Override
    public void deleteNotification(final Context context, final long notificationId, final NotificationsServiceCallback<Boolean> callback) {
        String url = ZWayNetworkHelper.getZwayNotificationDeleteUrl(context, notificationId);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (VolleyResponseHelper.hasResponseReturnedOK(response)) {
                            callback.onLoaded(true);
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
                        }
                        // Volley cant handle empty 204 responses. So we need this hack for now.
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
            public Map<String, String> getHeaders() {
                return ZWayNetworkHelper.getAuthenticationHeaders(context);
            }
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);

    }

    private void getNotifications(final Context context, final String deviceIdForMatch, final NotificationsServiceCallback<List<Notification>> callback) {
        String url = ZWayNetworkHelper.getZwayNotificationsUrl(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<Notification> notifications = new ArrayList<>();
                        try {
                            // Verify response code
                            if (VolleyResponseHelper.hasResponseReturnedOK(response)) {

                                JSONObject dataObjects = response.getJSONObject("data");
                                JSONArray notificationsArray = dataObjects.getJSONArray("notifications");

                                for (int i = 0; i < notificationsArray.length(); i++) {
                                    try {
                                        JSONObject notificationObject = notificationsArray.getJSONObject(i);
                                        long notificationId = notificationObject.getLong("id");
                                        String deviceId = notificationObject.getString("source");
                                        // If we are retrieving notifications for a specific device,
                                        // we only add the matching ones
                                        if (deviceIdForMatch == null || deviceIdForMatch.equals(deviceId)) {
                                            boolean redeemed = notificationObject.getBoolean("redeemed");
                                            DateFormat notificationsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
                                            notificationsFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            Date timestamp = notificationsFormat.parse(notificationObject.getString("timestamp"));
                                            JSONObject messageObject = notificationObject.getJSONObject("message");
                                            String deviceName = messageObject.getString("dev");
                                            String message = messageObject.getString("l");

                                            Notification notification = new Notification(notificationId, timestamp, deviceId, deviceName,
                                                    message, redeemed);
                                            notifications.add(notification);
                                        }
                                    } catch (JSONException e) {
                                        // Could not parse json objects
                                        LogHelper.logDebug(context, TAG, e.getMessage());
                                    } catch (ParseException ex) {
                                        // Could not parse timestamp
                                        callback.onError(context.getString(R.string.error_parsing));
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Sort notifications on timestamp
                        Collections.sort(notifications, new NotificationTimestampComparator());
                        callback.onLoaded(notifications);

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
