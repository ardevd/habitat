package no.aegisdynamics.habitat.devicedetail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import no.aegisdynamics.habitat.R;


public class DeviceDetailActivity extends AppCompatActivity{

    public static final String EXTRA_DEVICE_ID = "DEVICE_ID";
    public static final String EXTRA_DEVICE_TITLE = "DEVICE_TITLE";
    public static final String EXTRA_DEVICE_STATE = "DEVICE_STATE";
    public static final String EXTRA_DEVICE_NOTATION = "DEVICE_NOTATION";
    public static final String EXTRA_DEVICE_PROBE_TITLE = "DEVICE_PROBE_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicedetail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (null == savedInstanceState) {
            String deviceId = getIntent().getStringExtra(EXTRA_DEVICE_ID);
            String deviceState = getIntent().getStringExtra(EXTRA_DEVICE_STATE);
            String deviceNotation = getIntent().getStringExtra(EXTRA_DEVICE_NOTATION);
            String deviceProbeTitle = getIntent().getStringExtra(EXTRA_DEVICE_PROBE_TITLE);
            this.setTitle(getIntent().getStringExtra(EXTRA_DEVICE_TITLE));
            initFragment(DeviceDetailFragment.newInstance(deviceId, deviceState, deviceNotation,
                    deviceProbeTitle));
        }
    }

    private void initFragment(Fragment deviceDetailFragment) {
        // Add DeviceFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, deviceDetailFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
