package no.aegisdynamics.habitat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import no.aegisdynamics.habitat.R;

/**
 * Simple helper class to retrieve nightmode setting based on SharedPrefs
 */

public class NightModeSetting {

    private NightModeSetting() {
        // Added required empty constructor
    }

    public static int getNightModePreference(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String nightMode = settings.getString("nightmode", context.getString(R.string.prefs_night_mode_default));
        switch (nightMode) {
            case "off":
                return AppCompatDelegate.MODE_NIGHT_NO;
            case "on":
                return AppCompatDelegate.MODE_NIGHT_YES;
            case "auto":
                return AppCompatDelegate.MODE_NIGHT_AUTO;
            default:
                return AppCompatDelegate.MODE_NIGHT_NO;
        }
    }
}
