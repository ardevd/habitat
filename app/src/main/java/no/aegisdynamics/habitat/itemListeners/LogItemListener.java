package no.aegisdynamics.habitat.itemListeners;

import no.aegisdynamics.habitat.data.log.HabitatLog;

public interface LogItemListener {

    void onLogItemClicked(HabitatLog clickedLogEntry);

    void onLogDeleteClicked(HabitatLog clickedLogEntry);
}
