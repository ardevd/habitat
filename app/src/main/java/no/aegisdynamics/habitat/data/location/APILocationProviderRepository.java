package no.aegisdynamics.habitat.data.location;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.data.location.LocationsRepository;
import no.aegisdynamics.habitat.data.location.LocationsServiceApi;

/**
 * Concrete implementation to load locations from ZAutomation API
 */
public class APILocationProviderRepository implements LocationsRepository {

    private final LocationsServiceApi mLocationsServiceApi;
    private final Context mContext;

    public APILocationProviderRepository(@NonNull LocationsServiceApi locationsServiceApi, @NonNull Context context) {
        mLocationsServiceApi = locationsServiceApi;
        mContext = context;
    }

    @Override
    public void getLocations(@NonNull final LoadLocationsCallback callback) {
        mLocationsServiceApi.getAllLocations(mContext, new LocationsServiceApi.LocationsServiceCallback<List<Location>>() {

            @Override
            public void onLoaded(List<Location> locations) {
                callback.onLocationsLoaded(locations);
            }

            @Override
            public void onError(String error) {
                callback.onLocationsLoadError(error);
            }
        });
    }

    @Override
    public void getLocation(int locationId, final @NonNull GetLocationCallback callback) {
        mLocationsServiceApi.getLocation(mContext, locationId, new LocationsServiceApi.LocationsServiceCallback<Location>() {

            @Override
            public void onLoaded(Location locations) {
                callback.onLocationLoaded(locations);
            }

            @Override
            public void onError(String error) {
                callback.onLocationLoadError(error);
            }
        });
    }

    @Override
    public void createLocation(@NonNull Location mLocation, @NonNull final CreateLocationCallback callback) {
        mLocationsServiceApi.createLocation(mContext, mLocation, new LocationsServiceApi.LocationsServiceCallback<Boolean>() {
            @Override
            public void onLoaded(Boolean Locations) {
                callback.onLocationCreated();
            }

            @Override
            public void onError(String error) {
                callback.onLocationCreationFailed(error);
            }
        });
    }

    @Override
    public void updateLocation(int locationId, @NonNull String title, @NonNull final UpdateLocationCallback callback) {
        mLocationsServiceApi.updateLocation(mContext, locationId, title, new LocationsServiceApi.LocationsServiceCallback<Boolean>() {
            @Override
            public void onLoaded(Boolean Locations) {
                callback.onLocationUpdated();
            }

            @Override
            public void onError(String error) {
                callback.onLocationUpdateFailed(error);
            }
        });
    }

    @Override
    public void deleteLocation(int locationId, @NonNull final DeleteLocationCallback callback) {
        mLocationsServiceApi.removeLocation(mContext, locationId, new LocationsServiceApi.LocationsServiceCallback<Boolean>() {
            @Override
            public void onLoaded(Boolean Locations) {
                callback.onLocationDeleted();
            }

            @Override
            public void onError(String error) {
                callback.onLocationDeleteError(error);
            }
        });
    }

}
