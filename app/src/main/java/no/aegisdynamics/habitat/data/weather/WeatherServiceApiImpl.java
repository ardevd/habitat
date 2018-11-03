package no.aegisdynamics.habitat.data.weather;

import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.aegisdynamics.habitat.util.ErrorParserHelper;
import no.aegisdynamics.habitat.util.RequestQueueSingelton;

/**
 * Implementation of the Weather Service API that communicates with the OpenWeather API.
 */
public class WeatherServiceApiImpl implements WeatherServiceApi {

    @Override
    public void getWeather(final Context context, double lat, double lon, final WeatherServiceCallback<Weather> callback) {
        String API_KEY = new String(Base64.decode(getOwmApiKey(), Base64.DEFAULT));
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s",
                String.valueOf(lat), String.valueOf(lon), API_KEY);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verify response code
                            int responseCode = response.getInt("cod");
                            if (responseCode == 200) {

                                JSONArray weatherData = response.getJSONArray("weather");
                                int weatherConditionCode = 0;
                                String weatherCondition = "";
                                String weatherConditionIcon = "";
                                for (int i = 0; i < weatherData.length(); i++) {
                                    JSONObject weatherObject = weatherData.getJSONObject(i);
                                    weatherConditionCode = weatherObject.getInt("id");
                                    weatherCondition = weatherObject.getString("main");
                                    weatherConditionIcon = weatherObject.getString("icon");
                                }
                                JSONObject mainData = response.getJSONObject("main");
                                double weatherTemperature = mainData.getDouble("temp");
                                double weatherTemperatureMax = mainData.getDouble("temp_max");
                                double weatherTemperatureMin = mainData.getDouble("temp_min");

                                callback.onLoaded(new Weather(weatherConditionCode,weatherCondition,
                                        weatherConditionIcon, weatherTemperature,
                                        weatherTemperatureMax, weatherTemperatureMin));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(ErrorParserHelper.parseErrorToErrorMessage(context, e));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(ErrorParserHelper.parseErrorToErrorMessage(context, error));
                    }
                }) {
        };

        // Access the RequestQueue through your singleton class.
        RequestQueueSingelton.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    static {
        System.loadLibrary("owm");
    }

    public native String getOwmApiKey();
}
