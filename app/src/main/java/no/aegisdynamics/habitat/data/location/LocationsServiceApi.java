package no.aegisdynamics.habitat.data.location;

import android.content.Context;

import java.util.List;

/**
 * Defines an interface to the service API that is used by this application. All location data requests should
 * be piped through this interface.
 */

interface LocationsServiceApi {

    interface LocationsServiceCallback<T> {
        void onLoaded (T Locations);
        void onError (String error);
    }

    void getAllLocations(Context context, LocationsServiceApi.LocationsServiceCallback<List<Location>> callback);

    void getLocation(Context context, int locationId, LocationsServiceApi.LocationsServiceCallback<Location> callback);

    void createLocation(Context context, Location location, LocationsServiceApi.LocationsServiceCallback<Boolean> callback);

    void updateLocation(Context context, int locationId, String locationTitle, LocationsServiceApi.LocationsServiceCallback<Boolean> callback);

    void removeLocation(Context context, int locationId, LocationsServiceApi.LocationsServiceCallback<Boolean> callback);

}
