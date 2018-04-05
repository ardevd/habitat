package no.aegisdynamics.habitat.data.location;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing location data.
 */

public interface LocationsRepository {

    interface LoadLocationsCallback {

        void onLocationsLoaded(List<Location> locations);

        void onLocationsLoadError(String error);
    }

    interface GetLocationCallback {

        void onLocationLoaded(Location Location);

        void onLocationLoadError(String error);
    }

    interface CreateLocationCallback {
        void onLocationCreated();
        void onLocationCreationFailed(String error);
    }

    interface UpdateLocationCallback {
        void onLocationUpdated();
        void onLocationUpdateFailed(String error);
    }

    interface DeleteLocationCallback {
        void onLocationDeleted();
        void onLocationDeleteError(String error);
    }

    void getLocations(@NonNull LoadLocationsCallback callback);

    void getLocation(int locationId, @NonNull GetLocationCallback callback);

    void createLocation(@NonNull Location mLocation, @NonNull CreateLocationCallback callback);

    void updateLocation(int locationId, @NonNull String title, @NonNull UpdateLocationCallback callback);

    void deleteLocation(int locationId, @NonNull DeleteLocationCallback callback);

}