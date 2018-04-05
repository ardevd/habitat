package no.aegisdynamics.habitat.data.weather;


import android.support.annotation.NonNull;

public interface WeatherRepository {

    interface GetWeatherCallback {
        void onWeatherLoaded(Weather weather);
        void onWeatherLoadError(String error);
    }

    void getWeather(double lat, double lon, @NonNull GetWeatherCallback callback);
}
