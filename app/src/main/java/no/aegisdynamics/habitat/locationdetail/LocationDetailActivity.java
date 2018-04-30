package no.aegisdynamics.habitat.locationdetail;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import no.aegisdynamics.habitat.R;

import static no.aegisdynamics.habitat.util.PaletteHelper.checkVibrantSwatch;
import static no.aegisdynamics.habitat.util.PaletteHelper.createPaletteSync;

public class LocationDetailActivity extends AppCompatActivity implements LocationDetailContract.Activity {

    public static final String EXTRA_LOCATION_ID = "LOCATION_ID";
    public static final String EXTRA_LOCATION_TITLE = "LOCATION_TITLE";
    public CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationdetail);
        // Set up the toolbar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        if (null == savedInstanceState) {
            // Get the requested location id
            int locationId = getIntent().getIntExtra(EXTRA_LOCATION_ID, 0);
            String locationName = getIntent().getStringExtra(EXTRA_LOCATION_TITLE);
            // Set title of Detail page
            collapsingToolbar.setTitle(locationName);
            initFragment(LocationDetailFragment.newInstance(locationId, locationName));
        }

        // Hide the fab when the recyclerView (inside our nested scroll view) is scrolled down from the top
        NestedScrollView nestedScrollView = findViewById(R.id.locationDetailContentFrame);
        final FloatingActionButton fab = findViewById(R.id.fab_edit_location);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    private void setToolbarColorPalette(Bitmap bitmap) {
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = checkVibrantSwatch(p);

        // Set the toolbar background and text colors
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (vibrantSwatch != null) {
            collapsingToolbar.setExpandedTitleColor(vibrantSwatch.getBodyTextColor());

        }
    }

    private void initFragment(Fragment LocationDetailFragment) {
        // Add LocationDetailFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.locationDetailContentFrame, LocationDetailFragment);
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

    @Override
    public void updateToolbarColors(Bitmap bitmap) {
        setToolbarColorPalette(bitmap);
    }
}
