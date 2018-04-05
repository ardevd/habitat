package no.aegisdynamics.habitat.locationadd;


import android.support.annotation.NonNull;

import no.aegisdynamics.habitat.data.location.Location;

public interface LocationAddContract {

    interface View {

        void showEmptyLocationError();

        void showLocationAdded();

        void locationAddError(String error);

    }

    interface UserActionsListener {

        void saveLocation(@NonNull Location location);
    }
}
