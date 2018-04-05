package no.aegisdynamics.habitat.preferences;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import no.aegisdynamics.habitat.R;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    public static PrefsFragment newInstance() {
        return new PrefsFragment();
    }

}
