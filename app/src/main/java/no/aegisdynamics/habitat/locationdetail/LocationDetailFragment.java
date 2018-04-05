package no.aegisdynamics.habitat.locationdetail;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.DevicesAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.devicedetail.DeviceDetailActivity;
import no.aegisdynamics.habitat.itemListeners.DeviceCommandListener;
import no.aegisdynamics.habitat.itemListeners.DeviceItemListener;
import no.aegisdynamics.habitat.util.SnackbarHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;


public class LocationDetailFragment extends Fragment implements LocationDetailContract.View {

    public static final String ARGUMENT_LOCATION_ID = "LOCATION_ID";
    public static final String ARGUMENT_LOCATION_TITLE = "LOCATION_TITLE";

    private ImageView mLocationImageView;
    private DevicesAdapter mListAdapter;
    private int locationId;

    private LocationDetailContract.UserActionsListener mActionsListener;

    public LocationDetailFragment() {
        // Required empty constructor
    }
    public static LocationDetailFragment newInstance(int locationId, String locationName) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_LOCATION_ID, locationId);
        arguments.putString(ARGUMENT_LOCATION_TITLE, locationName);
        LocationDetailFragment fragment = new LocationDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new LocationDetailPresenter(Injection.provideDevicesRepository(getContext()),
                Injection.provideLocationsRepository(getContext()), this);
        mListAdapter = new DevicesAdapter(new ArrayList<Device>(0), mItemListener, mCommandListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locationdetail, container, false);
        mLocationImageView = getActivity().findViewById(R.id.activity_locationdetail_image);
        RecyclerView recyclerView = root.findViewById(R.id.locationdetail_events_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_devices_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditLocationDialog();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationId = getArguments().getInt(ARGUMENT_LOCATION_ID);
        mActionsListener.openLocation(locationId);
        mActionsListener.loadDevicesForLocation(locationId);
    }

    private DeviceItemListener mItemListener = new DeviceItemListener() {

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
    public void setProgressIndicator(final boolean active) {

    }

    @Override
    public void showLocationLoadError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }

    @Override
    public void showMissingLocation() {

    }

    @Override
    public void hideTitle() {

    }

    @Override
    public void showTitle(String title) {
        if (!isDetached()) {
            ((LocationDetailActivity) getActivity()).collapsingToolbar.setTitle(title);
        }
    }

    @Override
    public void showImage(String imageName) {
        if (isAdded()) {
            String imageUrl = ZWayNetworkHelper.getZwayLocationImageUrl(getContext().getApplicationContext(), imageName);
            Glide.with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(new BitmapImageViewTarget(mLocationImageView) {

                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                            if (getActivity() instanceof LocationDetailContract.Activity) {
                                ((LocationDetailContract.Activity) getActivity()).updateToolbarColors(resource);
                            }

                        }
                    });
        }

    }

    @Override
    public void hideImage() {
        mLocationImageView.setImageDrawable(null);
        mLocationImageView.setVisibility(View.GONE);

    }

    @Override
    public void hideDeviceCount() {

    }

    @Override
    public void showDeviceCount(String description) {

    }

    @Override
    public void showDevices(List<Device> devices) {
        mListAdapter.replaceData(devices);
    }

    @Override
    public void showDevicesLoadError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }

    @Override
    public void showCommandFailedMessage(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        delayedRefresh();
    }

    private void delayedRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 1 second
                mActionsListener.loadDevicesForLocation(locationId);
            }
        }, 1000);
    }

    @Override
    public void showCommandStatusMessage(boolean success) {
        String status_message = getString(R.string.devices_command_ok);
        if (!success) {
            status_message = getString(R.string.devices_command_error);
        }
        SnackbarHelper.showSimpleSnackbarMessage(status_message, getView());
        delayedRefresh();
    }

    @Override
    public void showDeviceDetailUI(@NonNull Device device) {
        // in it's own Activity, since it makes more sense that way
        Intent intent = new Intent(getContext(), DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_ID, device.getId());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_TITLE, device.getTitle());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_STATE, device.getStatus());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_NOTATION, device.getStatusNotation());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_PROBE_TITLE, device.getDeviceProbeTitle());
        startActivity(intent);
    }

    @Override
    public void showEditLocationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_location, null);
        alertDialog.setView(dialogView);
        TextView dialogSubtitle = dialogView.findViewById(R.id.edit_location_subtitle);
        final EditText locationInput = dialogView.findViewById(R.id.edit_location_input);
        dialogSubtitle.setText(getArguments().getString(ARGUMENT_LOCATION_TITLE));
            /* When positive (yes/ok) is clicked */
        alertDialog.setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String updatedTitle = locationInput.getText().toString();
                mActionsListener.editLocation(locationId, updatedTitle);
            }
        });

        /* When negative (No/cancel) button is clicked*/
        alertDialog.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Your custom code
            }
        });
        alertDialog.show();
    }

    @Override
    public void showLocationUpdated() {
        // Location updated. Refresh data.
        mActionsListener.openLocation(locationId);
        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.locationdetail_location_updated), getView());
    }

    @Override
    public void showLocationUpdateError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }
}
