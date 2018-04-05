package no.aegisdynamics.habitat.itemListeners;

import no.aegisdynamics.habitat.data.location.Location;


public interface LocationItemListener {
    void onLocationClick(Location clickedLocation);

    void onLocationDeleteClick(Location clickedLocation);
}