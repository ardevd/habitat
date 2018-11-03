package no.aegisdynamics.habitat.data.weather;


import android.content.Context;
import android.support.annotation.NonNull;

public class WeatherRepositories {

    private WeatherRepositories() {
        // Required empty instance
    }

    public static synchronized WeatherRepository getRepository(@NonNull Context context,
                                                               @NonNull WeatherServiceApi weatherServiceApi) {
        return new APIWeatherProviderRepository(weatherServiceApi, context);
    }
}
