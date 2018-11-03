package no.aegisdynamics.habitat.devices;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.DevicesAdapter;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.itemListeners.DeviceCommandListener;
import no.aegisdynamics.habitat.itemListeners.DeviceItemListener;
import no.aegisdynamics.habitat.util.FavoritesHelper;
import no.aegisdynamics.habitat.util.HabitatNavigator;
import no.aegisdynamics.habitat.util.SnackbarHelper;

public class DevicesFragment extends Fragment implements DevicesContract.View, SearchView.OnQueryTextListener {
    private DevicesAdapter mListAdapter;
    private DevicesContract.UserActionsListener mActionsListener;

    private SharedPreferences.Editor editor;

    private boolean favoritesOnly = false;

    public DevicesFragment() {
        // Required empty constructor
    }

    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new DevicesPresenter(Injection.provideDevicesRepository(getContext()), this);
        mListAdapter = new DevicesAdapter(new ArrayList<Device>(0), mItemListener, mCommandListener);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_devices, menu);
        MenuItem favoriteItem = menu.findItem(R.id.menu_devices_show_favorites);
        favoriteItem.setChecked(favoritesOnly);
        MenuItem searchItem = menu.findItem(R.id.menu_devices_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.devices_menu_search_hint));
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_devices_show_favorites:
                if (item.isChecked()) {
                    item.setChecked(false);
                    favoritesOnly = false;
                } else {
                    item.setChecked(true);
                    favoritesOnly = true;
                }
                editor.putBoolean("devices_favorites_only", favoritesOnly).apply();
                mActionsListener.loadDevices();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadDevices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_devices, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.devices_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_devices_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = settings.edit();

        favoritesOnly = settings.getBoolean("devices_favorites_only", false);

        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout_devices);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadDevices();
            }
        });


        return root;
    }


    /**
     * Listener for clicks on devices in the RecyclerView.
     */
    private final DeviceItemListener mItemListener = new DeviceItemListener() {

        @Override
        public void onDeviceClick(Device clickedDevice) {
            mActionsListener.openDeviceDetails(clickedDevice);
        }
    };

    /**
     * Listener for commands to devices.
     */
    private final DeviceCommandListener mCommandListener = new DeviceCommandListener() {
        @Override
        public void onCommand(Device commandedDevice, String command) {
            mActionsListener.sendCommand(commandedDevice, command);
        }
    };

    @Override
    public void showDevices(List<Device> devices) {
        if (isAdded()) {
            if (favoritesOnly) {
                // Redo the list of devices, removing non favorites
                Set<String> favorites = FavoritesHelper.getFavoriteDevicesFromPrefs(getContext());
                List<Device> favoriteDevices = new ArrayList<>();
                for (Device device : devices) {
                    if (favorites.contains(device.getId())) {
                        favoriteDevices.add(device);
                    }
                    devices = favoriteDevices;
                }
            }
            // Update subtitle
            TextView devicesSubtitle = getView().findViewById(R.id.devices_header_devices);
            devicesSubtitle.setText(getString(R.string.devices_num_devices, devices.size()));
            // Replace adapter data with the new list of devices
            mListAdapter.replaceData(devices);
        }
    }

    @Override
    public void showDevicesLoadError(String error) {
        if (isAdded()) {
            SnackbarHelper.showFlashbarErrorMessage(getString(R.string.devices_generic_error_title),
                    error, getActivity());
        }
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_devices);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });

    }

    @Override
    public void showCommandStatusMessage(boolean success) {
        if (isAdded()) {
            String statusMessage = getString(R.string.devices_command_ok);
            if (!success) {
                statusMessage = getString(R.string.devices_command_error);
            }
            SnackbarHelper.showSimpleSnackbarMessage(statusMessage, getView());
            delayedRefresh();
        }
    }

    private void delayedRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 1 second
                mActionsListener.loadDevices();
            }
        }, 3000);
    }

    @Override
    public void showCommandFailedMessage(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
            delayedRefresh();
        }
    }

    @Override
    public void showDeviceDetailUI(@NonNull Device device) {
        HabitatNavigator.showDeviceDetailUI(device, getContext());
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mListAdapter.getFilter().filter(s);
        return false;
    }
}
