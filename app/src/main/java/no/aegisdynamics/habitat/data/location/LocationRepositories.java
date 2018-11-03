package no.aegisdynamics.habitat.data.location;


import android.content.Context;
import android.support.annotation.NonNull;

public class LocationRepositories {

    private LocationRepositories() {
        // No instance
    }

    public static synchronized LocationsRepository getRepository(@NonNull Context context,
                                                                 @NonNull LocationsServiceApi locationsServiceApi) {
        return new APILocationProviderRepository(locationsServiceApi, context);

    }

}

