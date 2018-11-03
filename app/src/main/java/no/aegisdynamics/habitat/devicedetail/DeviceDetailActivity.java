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
import no.aegisdynamics.habitat.data.device.Device;


public class DeviceDetailActivity extends AppCompatActivity{

    public static final String EXTRA_DEVICE = "device";

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
            Device device = getIntent().getParcelableExtra(EXTRA_DEVICE);
            this.setTitle(device.getTitle());
            initFragment(DeviceDetailFragment.newInstance(device));
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
