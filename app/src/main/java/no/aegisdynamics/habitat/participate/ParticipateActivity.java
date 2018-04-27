package no.aegisdynamics.habitat.participate;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.base.BaseActivity;
import no.aegisdynamics.habitat.log.LogActivity;
import no.aegisdynamics.habitat.util.LogHelper;

/**
 * Activity class for the Participate screen.
 */

public class ParticipateActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_participate);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }


        // Check navigation drawer menu item.
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.menu_participate).setChecked(true);

        // Get Version information
        String version = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogHelper.logError(this, "ParticipateActivity", "Could not get Habitat package info");
        }

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .addItem(new Element().setTitle(String.format("%s %s", getString(R.string.participate_version), version)))
                .addGroup(getString(R.string.participate_connect))
                .addPlayStore("no.aegisdynamics.habitat", getString(R.string.participate_play))
                .addGitHub("ardevd/Habitat", getString(R.string.participate_github))
                .addGroup(getString(R.string.participate_debug))
                .addItem(getLogsElement())
                .addItem(getCopyRightsElement())
                .setDescription(getString(R.string.participate_description))
                .create();

        // add about page to frame layout
        FrameLayout frameLayout = findViewById(R.id.contentFrame);
        frameLayout.addView(aboutPage);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copyright), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.ic_copyright);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ParticipateActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    Element getLogsElement() {
        Element logElement = new Element();
        logElement.setTitle(getString(R.string.participate_logs));
        logElement.setIconDrawable(R.drawable.ic_info);
        logElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        logElement.setIconNightTint(android.R.color.white);
        logElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogsActivity();
            }
        });

        return logElement;
    }

    private void showLogsActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }
}
