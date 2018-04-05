package no.aegisdynamics.habitat.dashboard;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.base.BaseActivity;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

/**
 * Activity for the Dashboard screen
 */

public class DashboardActivity extends BaseActivity implements DashboardContract.Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dashboard);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }

        if (null == savedInstanceState) {
            initFragment(DashboardFragment.newInstance());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Check navigation drawer menu item.
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
    }

    private void initFragment(Fragment dashboardFragment) {
        // Add Dashboard Fragment to the layout
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, dashboardFragment);
        transaction.commit();
    }

    @Override
    public void showLocationAddHint() {
        // Show location button target hint
        new MaterialTapTargetPrompt.Builder(DashboardActivity.this)
                .setTarget(findViewById(R.id.dashboard_location_button))
                .setPrimaryText(getString(R.string.dashboard_set_location_hint))
                .setSecondaryText(getString(R.string.dashboard_set_location_hint_desc))
                .setBackgroundColour(ContextCompat.getColor(this, R.color.colorAccent))
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            // User has pressed the prompt target.
                        }
                    }
                })
                .show();
    }

    @Override
    public void showDevicesHint() {
        new MaterialTapTargetPrompt.Builder(DashboardActivity.this)
                .setTarget(findViewById(R.id.dashboard_no_devices_text))
                .setPrimaryText(getString(R.string.dashboard_devices_hint))
                .setSecondaryText(getString(R.string.dashboard_devices_hint_desc))
                .setBackgroundColour(ContextCompat.getColor(this, R.color.colorAccent))
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            // User has pressed the prompt target
                        }
                    }
                })
                .show();
    }
}
