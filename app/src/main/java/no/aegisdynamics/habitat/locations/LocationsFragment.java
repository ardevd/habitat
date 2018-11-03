package no.aegisdynamics.habitat.locations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.LocationsAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.itemListeners.LocationItemListener;
import no.aegisdynamics.habitat.locationadd.LocationAddActivity;
import no.aegisdynamics.habitat.locationdetail.LocationDetailActivity;
import no.aegisdynamics.habitat.util.SnackbarHelper;


public class LocationsFragment extends Fragment implements LocationsContract.View {
    private LocationsAdapter mListAdapter;
    private LocationsContract.UserActionsListener mActionsListener;

    public LocationsFragment() {
        // Required empty constructor
    }

    public static LocationsFragment newInstance() {
        return new LocationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new LocationsPresenter(Injection.provideLocationsRepository(getContext()), this);
        mListAdapter = new LocationsAdapter(new ArrayList<Location>(0), mItemListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locations, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.locations_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_locations_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_new_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.addLocation();
            }
        });


        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout_locations);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadLocations();
            }
        });
        return root;
    }

    /**
     * Listener for clicks on locations in the RecyclerView.
     */
    private final LocationItemListener mItemListener = new LocationItemListener() {

        @Override
        public void onLocationClick(Location clickedLocation) {
            mActionsListener.openLocationDetails(clickedLocation);
        }

        @Override
        public void onLocationDeleteClick(Location clickedLocation) {
            // Delete location.
            mActionsListener.deleteLocation(clickedLocation);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadLocations();
    }


    @Override
    public void showLocations(List<Location> locations) {
        mListAdapter.replaceData(locations);
    }

    @Override
    public void showLocationsLoadError(String error) {
        if (isAdded()) {
            SnackbarHelper.showFlashbarErrorMessage(getString(R.string.locations_generic_error_title),
                    error, getActivity());
        }
    }

    @Override
    public void showAddLocation() {
        Intent intent = new Intent(getContext(), LocationAddActivity.class);
        startActivity(intent);
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_locations);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showLocationDetailUI(Location location) {
        // in it's own Activity, since it makes more sense that way
        Intent intent = new Intent(getContext(), LocationDetailActivity.class);
        intent.putExtra(LocationDetailActivity.EXTRA_LOCATION, location);
        startActivity(intent);
    }

    @Override
    public void showDeleteLocation() {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.location_deleted), getView());
            mActionsListener.loadLocations();
        }
    }

    @Override
    public void showDeleteLocationError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
            mActionsListener.loadLocations();
        }
    }

}