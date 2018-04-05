package no.aegisdynamics.habitat.automations;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.base.BaseActivity;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class AutomationsActivity extends BaseActivity implements AutomationsContract.Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_automation);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }

        if (null == savedInstanceState) {
            initFragment(AutomationsFragment.newInstance());
        }

        // Check navigation drawer menu item.
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.menu_automation).setChecked(true);
    }

    private void initFragment(Fragment automationFragment) {
        // Add DeviceFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, automationFragment);
        transaction.commit();
    }

    @Override
    public void showFabHint() {
        // Show FAB target hint
        new MaterialTapTargetPrompt.Builder(AutomationsActivity.this)
                .setTarget(findViewById(R.id.fab_new_automation))
                .setPrimaryText(getString(R.string.automation_target_title))
                .setSecondaryText(getString(R.string.automation_target_subtitle))
                .setBackgroundColour(ContextCompat.getColor(this, R.color.colorAccent))
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