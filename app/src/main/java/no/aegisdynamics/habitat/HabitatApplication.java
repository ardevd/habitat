package no.aegisdynamics.habitat;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import no.aegisdynamics.habitat.util.NightModeSetting;

/**
 * Custom Application Class
 */

public class HabitatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(NightModeSetting.getNightModePreference(getApplicationContext()));
    }
}
