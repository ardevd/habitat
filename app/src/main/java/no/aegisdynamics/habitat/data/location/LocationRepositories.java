package no.aegisdynamics.habitat.data.location;


import android.content.Context;
import android.support.annotation.NonNull;

public class LocationRepositories {

    private LocationRepositories() {
        // No instance
    }

    private static LocationsRepository repository = null;

    public synchronized static LocationsRepository getRepository(@NonNull Context context,
                                                                 @NonNull LocationsServiceApi locationsServiceApi) {
        repository = new APILocationProviderRepository(locationsServiceApi, context);
        return repository;
    }

}

