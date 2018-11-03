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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.DevicesAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.devicedetail.DeviceDetailActivity;
import no.aegisdynamics.habitat.itemListeners.DeviceCommandListener;
import no.aegisdynamics.habitat.itemListeners.DeviceItemListener;
import no.aegisdynamics.habitat.util.GlideApp;
import no.aegisdynamics.habitat.util.SnackbarHelper;
import no.aegisdynamics.habitat.zautomation.ZWayNetworkHelper;


public class LocationDetailFragment extends Fragment implements LocationDetailContract.View {

    private ImageView mLocationImageView;
    private DevicesAdapter mListAdapter;
    private Location detailedLocation;

    private static final String ARGUMENT_LOCATION = "location";

    private LocationDetailContract.UserActionsListener mActionsListener;

    public LocationDetailFragment() {
        // Required empty constructor
    }

    public static LocationDetailFragment newInstance(Location location) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_LOCATION, location);
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
        RecyclerView recyclerView = root.findViewById(R.id.locationdetail_devices_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_devices_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        // Set up floating action button
        final FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_location);
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
        detailedLocation = getArguments().getParcelable(ARGUMENT_LOCATION);
        showDeviceCount(detailedLocation.getDeviceCount());
        mActionsListener.openLocation(detailedLocation.getId());
        mActionsListener.loadDevicesForLocation(detailedLocation.getId());
    }

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
        if (isAdded()) {
            ((LocationDetailActivity) getActivity()).collapsingToolbar.setTitle(title);
        }
    }

    @Override
    public void showImage(String imageName) {
        if (isAdded()) {
            String imageUrl = ZWayNetworkHelper.getZwayLocationImageUrl(getContext().getApplicationContext(), imageName);
            GlideApp.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(new ImageViewTarget<Bitmap>(mLocationImageView) {
                        @Override
                        protected void setResource(@Nullable Bitmap resource) {
                            if (resource != null) {
                                super.view.setImageBitmap(resource);
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
    public void showDeviceCount(int deviceCount) {
        if (isAdded() && getView() != null) {
            TextView deviceTitle = getView().findViewById(R.id.locationdetail_devices_title);
            if (deviceCount > 0) {
                deviceTitle.setText(String.format(Locale.getDefault(), "%s (%d)",
                        getString(R.string.locationdetail_devices), deviceCount));
            } else {
                deviceTitle.setText(getString(R.string.locationdetail_no_devices));
            }
        }
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
                mActionsListener.loadDevicesForLocation(detailedLocation.getId());
            }
        }, 1000);
    }

    @Override
    public void showCommandStatusMessage(boolean success) {
        if (isAdded()) {
            String status_message = getString(R.string.devices_command_ok);
            if (!success) {
                status_message = getString(R.string.devices_command_error);
            }
            SnackbarHelper.showSimpleSnackbarMessage(status_message, getView());
            delayedRefresh();
        }
    }

    @Override
    public void showDeviceDetailUI(@NonNull Device device) {
        // in it's own Activity, since it makes more sense that way
        Intent intent = new Intent(getContext(), DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE, device);
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
        dialogSubtitle.setText(detailedLocation.getTitle());
        /* When positive (yes/ok) is clicked */
        alertDialog.setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String updatedTitle = locationInput.getText().toString();
                mActionsListener.editLocation(detailedLocation.getId(), updatedTitle);
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
        if (isAdded()) {
            // Location updated. Refresh data.
            mActionsListener.openLocation(detailedLocation.getId());
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.locationdetail_location_updated), getView());
        }
    }

    @Override
    public void showLocationUpdateError(String error) {
        if (isAdded()) {
            SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        }
    }
}
