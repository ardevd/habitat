package no.aegisdynamics.habitat.locationadd;


import android.support.annotation.NonNull;

import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.data.location.LocationsRepository;

public class LocationAddPresenter implements LocationAddContract.UserActionsListener {
    @NonNull
    private final LocationsRepository mLocationsRepository;
    @NonNull
    private final LocationAddContract.View mLocationAddView;
    
    public LocationAddPresenter(@NonNull LocationsRepository locationsRepository,
                                @NonNull LocationAddContract.View locationAddView) {

        mLocationsRepository = locationsRepository;
        mLocationAddView = locationAddView;
    }

    /**
     * Save and create a new location
     * @param mLocation location
     */
    @Override
    public void saveLocation(@NonNull Location mLocation) {

        if (mLocation.isEmpty()) {
            mLocationAddView.showEmptyLocationError();
        } else {
            mLocationsRepository.createLocation(mLocation, new LocationsRepository.CreateLocationCallback() {

                @Override
                public void onLocationCreated() {
                    mLocationAddView.showLocationAdded();
                }

                @Override
                public void onLocationCreationFailed(String error) {
                    mLocationAddView.locationAddError(error);
                }
            });
        }
    }
}
