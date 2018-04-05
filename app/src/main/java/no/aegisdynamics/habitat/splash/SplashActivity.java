package no.aegisdynamics.habitat.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import no.aegisdynamics.habitat.dashboard.DashboardActivity;
import no.aegisdynamics.habitat.setup.SetupActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If on first run, we launch the setup activity.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent;
        if (settings.getBoolean("first_run", true)) {
            intent = new Intent(this, SetupActivity.class);
        } else {
            intent = new Intent(this, DashboardActivity.class);
        }

        startActivity(intent);
        finish();
    }
}