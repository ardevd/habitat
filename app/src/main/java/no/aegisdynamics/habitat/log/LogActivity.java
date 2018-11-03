package no.aegisdynamics.habitat.log;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import no.aegisdynamics.habitat.R;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        // Set up the toolbar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (null == savedInstanceState) {
            this.setTitle(R.string.logs_title);
            initFragment(LogFragment.newInstance());
        }
    }

    private void initFragment(Fragment logFragment) {
        // Add logFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, logFragment);
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
