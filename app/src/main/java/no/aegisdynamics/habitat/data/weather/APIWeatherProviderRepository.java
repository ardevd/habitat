package no.aegisdynamics.habitat.data.weather;


import android.content.Context;
import android.support.annotation.NonNull;

public class APIWeatherProviderRepository implements WeatherRepository {

    private final WeatherServiceApi mWeatherServiceApi;
    private final Context mContext;

    public APIWeatherProviderRepository(@NonNull WeatherServiceApi weatherServiceApi,
                                        @NonNull Context context) {
        mWeatherServiceApi = weatherServiceApi;
        mContext = context;

    }

    @Override
    public void getWeather(double lat, double lon, @NonNull final GetWeatherCallback callback) {
        mWeatherServiceApi.getWeather(mContext, lat, lon, new WeatherServiceApi.WeatherServiceCallback<Weather>() {

            @Override
            public void onLoaded(Weather weather) {
                callback.onWeatherLoaded(weather);

            }

            @Override
            public void onError(String error) {
                callback.onWeatherLoadError(error);
            }
        });
    }
}
