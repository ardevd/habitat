package no.aegisdynamics.habitat.modules;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.base.BaseActivity;

public class ModulesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modules);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.menu_nav_modules);
        }

        if (null == savedInstanceState) {
            initFragment(ModulesFragment.newInstance());
        }

        // Check navigation drawer menu item.
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.menu_modules).setChecked(true);
    }

    private void initFragment(Fragment modulesFragment) {
        // Add ModulesFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, modulesFragment);
        transaction.commit();
    }
}
