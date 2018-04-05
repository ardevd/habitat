package no.aegisdynamics.habitat.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Locale;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.automations.AutomationsActivity;
import no.aegisdynamics.habitat.backup.BackupActivity;
import no.aegisdynamics.habitat.controller.ControllerActivity;
import no.aegisdynamics.habitat.dashboard.DashboardActivity;
import no.aegisdynamics.habitat.devices.DevicesActivity;
import no.aegisdynamics.habitat.notifications.NotificationsActivity;
import no.aegisdynamics.habitat.participate.ParticipateActivity;
import no.aegisdynamics.habitat.preferences.PrefsActivity;
import no.aegisdynamics.habitat.locations.LocationsActivity;
import no.aegisdynamics.habitat.setup.SetupActivity;
import no.aegisdynamics.habitat.util.IconHelper;
import no.aegisdynamics.habitat.util.UserCredentialsManager;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create Navigation drawer and inflate layout
        final NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer);
        TextView navHeaderTitle = navigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
        navHeaderTitle.setText(getZwayName());
        TextView navHeaderSubtitle = navigationView.getHeaderView(0).findViewById(R.id.nav_header_subtitle);
        navHeaderSubtitle.setText(String.format(Locale.getDefault(), "%s@%s",
                UserCredentialsManager.getZwayUsername(this), getZwayHostname()));
        // Set SSL indicator icon
        ImageView sslImage = navigationView.getHeaderView(0).findViewById(R.id.nav_header_ssl_icon);
        sslImage.setImageResource(IconHelper.getSSLIndicatorIcon(getSSLEnabledStatus()));
        ImageViewCompat.setImageTintList(sslImage, ColorStateList.valueOf(ContextCompat.getColor(this, IconHelper.getSSLIndicatorTint(getSSLEnabledStatus()))));
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            // Set the hamburger menu icon tint to white
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_24dp, null);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);

            supportActionBar.setHomeAsUpIndicator(drawable);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        // Set on click listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.menu_home:
                                Intent homeIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                                startActivity(homeIntent);
                                break;
                            case R.id.menu_devices:
                                Intent devicesIntent = new Intent(getApplicationContext(), DevicesActivity.class);
                                startActivity(devicesIntent);
                                break;
                            case R.id.menu_rooms:
                                Intent roomIntent = new Intent(getApplicationContext(), LocationsActivity.class);
                                startActivity(roomIntent);
                                break;
                            case R.id.menu_events:
                                Intent eventsIntent = new Intent(getApplicationContext(), NotificationsActivity.class);
                                startActivity(eventsIntent);
                                break;
                            case R.id.menu_automation:
                                Intent automationIntent = new Intent(getApplicationContext(), AutomationsActivity.class);
                                startActivity(automationIntent);
                                break;
                            case R.id.menu_controller:
                                Intent controllerIntent = new Intent(getApplicationContext(), ControllerActivity.class);
                                startActivity(controllerIntent);
                                break;
                            case R.id.menu_backup:
                                Intent backupIntent = new Intent(getApplicationContext(), BackupActivity.class);
                                startActivity(backupIntent);
                                break;
                            case R.id.menu_settings:
                                Intent prefsIntent = new Intent(getApplicationContext(), PrefsActivity.class);
                                startActivity(prefsIntent);
                                break;
                            case R.id.menu_participate:
                                Intent participateIntent = new Intent(getApplicationContext(), ParticipateActivity.class);
                                startActivity(participateIntent);
                                break;
                            case R.id.menu_logout:
                                wipeCredentialsAndLogout();
                                break;
                            default:
                                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.error_not_implemented), Snackbar.LENGTH_SHORT).show();
                                break;
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void wipeCredentialsAndLogout() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("first_run", true);
        editor.putString("zway_username", "");
        editor.putString("zway_password", "");
        editor.putString("zway_username_iv", "");
        editor.putString("zway_password_iv", "");
        editor.putString("home_lat", "");
        editor.putString("home_long", "");
        editor.putStringSet("favorite_devices", new HashSet<String>());
        editor.putStringSet("dashboard_devices", new HashSet<String>());
        editor.apply();
        Intent setupIntent = new Intent(getApplicationContext(), SetupActivity.class);
        setupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
    }

    private boolean getSSLEnabledStatus() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getBoolean("zway_ssl", false);
    }

    private String getZwayName() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("zway_name", "");
    }

    private String getZwayHostname() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String hostname = settings.getString("zway_hostname", "");

        return hostname.replace(":8083", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

}

