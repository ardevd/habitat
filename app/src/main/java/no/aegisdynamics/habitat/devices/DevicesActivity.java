package no.aegisdynamics.habitat.devices;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.base.BaseActivity;

public class DevicesActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_devices);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.menu_nav_devices);
        }

        if (null == savedInstanceState) {
            initFragment(DevicesFragment.newInstance());
        }

        // Check navigation drawer menu item.
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.menu_devices).setChecked(true);
    }

    private void initFragment(Fragment devicesFragment) {
        // Add DeviceFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, devicesFragment);
        transaction.commit();
    }
}
