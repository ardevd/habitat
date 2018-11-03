package no.aegisdynamics.habitat.data.weather;

import android.content.Context;

/**
 * Defines an interface to the weather service API. All weather requests should be piped
 * through this interface.
 */
interface WeatherServiceApi {

    interface WeatherServiceCallback<T> {
        void onLoaded(T weather);
        void onError(String error);
    }

    void getWeather(Context context, double lat, double lon, WeatherServiceApi.WeatherServiceCallback<Weather> callback);
}
