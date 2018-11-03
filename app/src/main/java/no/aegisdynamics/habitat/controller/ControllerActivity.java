package no.aegisdynamics.habitat.controller;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.base.BaseActivity;

/**
 * Activity for the Controller screen
 */

public class ControllerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_devices);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.controller_activity_title);
        }

        if (null == savedInstanceState) {
            initFragment(ControllerFragment.newInstance());
        }

        // Check navigation drawer menu item.
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.menu_controller).setChecked(true);
    }

    private void initFragment(Fragment controllerFragment) {
        // Add ControllerFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, controllerFragment);
        transaction.commit();
    }
}
