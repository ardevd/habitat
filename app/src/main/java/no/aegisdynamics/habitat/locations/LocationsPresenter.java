package no.aegisdynamics.habitat.locations;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.data.location.LocationsRepository;


/**
 * Listens to user actions from the UI ({@link LocationsFragment}), retrieves the data and updates the
 * UI as required.
 */
public class LocationsPresenter implements LocationsContract.UserActionsListener {

    private final LocationsRepository mLocationsRepository;
    private final LocationsContract.View mLocationsView;

    public LocationsPresenter(@NonNull LocationsRepository locationsRepository, @NonNull LocationsContract.View locationsView){
        mLocationsRepository = locationsRepository;
        mLocationsView = locationsView;
    }


    @Override
    public void loadLocations() {
        // Get list of locations from API server.
        mLocationsView.setProgressIndicator(true);
        mLocationsRepository.getLocations(new LocationsRepository.LoadLocationsCallback() {

            @Override
            public void onLocationsLoaded(List<Location> locations) {
                mLocationsView.setProgressIndicator(false);
                mLocationsView.showLocations(locations);
            }

            @Override
            public void onLocationsLoadError(String error) {
                mLocationsView.setProgressIndicator(false);
                mLocationsView.showLocationsLoadError(error);
            }
        });
    }

    @Override
    public void addLocation() {
        mLocationsView.showAddLocation();
    }

    @Override
    public void openLocationDetails(@NonNull Location requestedRoom) {
        mLocationsView.showLocationDetailUI(requestedRoom.getId(), requestedRoom.getTitle());
    }

    @Override
    public void deleteLocation(Location location) {
        mLocationsView.setProgressIndicator(true);
        mLocationsRepository.deleteLocation(location.getId(), new LocationsRepository.DeleteLocationCallback() {

            @Override
            public void onLocationDeleted() {
                mLocationsView.setProgressIndicator(false);
                mLocationsView.showDeleteLocation();
            }

            @Override
            public void onLocationDeleteError(String error) {
                mLocationsView.setProgressIndicator(false);
                mLocationsView.showDeleteLocationError(error);
            }
        });
    }
}