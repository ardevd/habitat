package no.aegisdynamics.habitat.locations;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.location.Location;

public interface LocationsContract {

    interface View {
        void showLocations(List<Location> locations);
        void showLocationsLoadError(String error);
        void showAddLocation();
        void setProgressIndicator(boolean active);
        void showLocationDetailUI(int locationId, String locationTitle);
        void showDeleteLocation();
        void showDeleteLocationError(String error);
    }
    interface UserActionsListener {
        void loadLocations();
        void addLocation();
        void openLocationDetails(@NonNull Location requestedLocation);
        void deleteLocation(Location location);
    }
}