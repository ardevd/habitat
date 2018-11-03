package no.aegisdynamics.habitat.devicedetail;


import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.db.chart.animation.Animation;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.util.Tools;
import com.db.chart.view.LineChartView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.feeeei.circleseekbar.CircleSeekBar;
import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.NotificationsAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.notifications.Notification;
import no.aegisdynamics.habitat.itemListeners.DeviceCommandListener;
import no.aegisdynamics.habitat.itemListeners.NotificationItemListener;
import no.aegisdynamics.habitat.provider.DeviceDataContract;
import no.aegisdynamics.habitat.util.DeviceStatusHelper;
import no.aegisdynamics.habitat.util.FavoritesHelper;
import no.aegisdynamics.habitat.util.SnackbarHelper;

public class DeviceDetailFragment extends Fragment implements DeviceDetailContract.View, DeviceDataContract {

    private static final String ARGUMENT_DEVICE = "device";
    private static final String LOG_TAG = "Habitat";
    private Device detailedDevice;
    private NotificationsAdapter mListAdapter;
    private DeviceDetailContract.UserActionsListener mActionsListener;

    private TextView tvLocation;

    private View inflatedSwitchBinaryControlLayout;
    private Switch switchBinary;
    private TextView tvSwitchStateOn;
    private TextView tvSwitchStateOff;

    private View inflatedSensorControlLayout;
    private TextView tvSensorValue;
    private TextView tvSensorNotation;
    private TextView tvSensorProbeTitle;

    private View inflatedSwitchMultilevelControlLayout;
    private TextView tvSwitchMultilevelValue;
    private SeekBar seekbarSwitchMultiLevel;
    private Switch switchMultilevel;

    private View inflatedBatteryControlLayout;
    private TextView tvBatteryValue;
    private ImageView ivBatteryIcon;

    private View inflatedThermostatControlLayout;
    private CircleSeekBar seekbarThermostat;

    private TextView tvThermostatValue;
    private TextView tvThermostatNotation;

    // Charts
    private Tooltip mTip;
    private LineChartView lineChart;

    private ImageButton ibFavorite;
    private boolean isFavorite = false;

    public DeviceDetailFragment() {
        // Required empty constructor
    }

    public static DeviceDetailFragment newInstance(Device device) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_DEVICE, device);
        DeviceDetailFragment fragment = new DeviceDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new DeviceDetailPresenter(Injection.provideDevicesRepository(getContext()),
                Injection.provideNotificationsRepository(getContext()), this);
        mListAdapter = new NotificationsAdapter(new ArrayList<Notification>(0), mItemListener);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_devicedetail, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.devicedetail_events_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_notifications_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        detailedDevice = getArguments().getParcelable(ARGUMENT_DEVICE);
        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout_devicedetail);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadNotificationsForDevice(detailedDevice.getId());
            }
        });

        LayoutInflater inflaterControlViews = LayoutInflater.from(getActivity());

        tvLocation = root.findViewById(R.id.devicedetail_location_title);

        ibFavorite = root.findViewById(R.id.devicedetail_controls_favimage);
        lineChart = root.findViewById(R.id.sensormultileveldetail_linechart);

        inflatedSwitchBinaryControlLayout = inflaterControlViews.inflate(R.layout.device_controls_switch_binary, container, false);
        switchBinary = inflatedSwitchBinaryControlLayout.findViewById(R.id.devicedetail_switchbinary_switch);
        tvSwitchStateOff = inflatedSwitchBinaryControlLayout.findViewById(R.id.devicedetail_switchbinary_state_off);
        tvSwitchStateOn = inflatedSwitchBinaryControlLayout.findViewById(R.id.devicedetail_switchbinary_state_on);

        inflatedSensorControlLayout = inflaterControlViews.inflate(R.layout.device_controls_sensor_multilevel, container, false);
        tvSensorValue = inflatedSensorControlLayout.findViewById(R.id.devicedetail_sensorMultilevel_value);
        tvSensorNotation = inflatedSensorControlLayout.findViewById(R.id.devicedetail_sensorMultilevel_notation);
        tvSensorProbeTitle = inflatedSensorControlLayout.findViewById(R.id.devicedetail_sensorMultilevel_probe_title);

        inflatedThermostatControlLayout = inflaterControlViews.inflate(R.layout.device_controls_thermostat, container, false);
        seekbarThermostat = inflatedThermostatControlLayout.findViewById(R.id.devicedetail_thermostat_seekbar);
        tvThermostatValue = inflatedThermostatControlLayout.findViewById(R.id.devicedetail_thermostat_value);
        tvThermostatNotation = inflatedThermostatControlLayout.findViewById(R.id.devicedetail_thermostat_notation);

        inflatedBatteryControlLayout = inflaterControlViews.inflate(R.layout.device_controls_battery, container, false);
        tvBatteryValue = inflatedBatteryControlLayout.findViewById(R.id.devicedetail_battery_value);
        ivBatteryIcon = inflatedBatteryControlLayout.findViewById(R.id.devicedetail_battery_icon);

        inflatedSwitchMultilevelControlLayout = inflaterControlViews.inflate(R.layout.device_controls_switch_multilevel, container, false);
        tvSwitchMultilevelValue = inflatedSwitchMultilevelControlLayout.findViewById(R.id.devicedetail_switchmultilevel_value);
        seekbarSwitchMultiLevel = inflatedSwitchMultilevelControlLayout.findViewById(R.id.devicedetail_switchmultilevel_seekbar);
        switchMultilevel = inflatedSwitchMultilevelControlLayout.findViewById(R.id.devicedetail_switchmultilevel_switch);

        // Configure the chart tooltip
        // TODO: Only do this with supported devices
        configureToolTip();

        return root;
    }

    /**
     * Listener for clicks on notifications in the RecyclerView.
     */
    private final NotificationItemListener mItemListener = new NotificationItemListener() {

        @Override
        public void onNotificationClick(Notification clickedNotification) {
        }

        @Override
        public void onNotificationDeleteClick(Notification clickedNotification) {
            mActionsListener.deleteNotification(clickedNotification);
        }

        @Override
        public void onNotificationRedeemClick(Notification clickedNotification) {
            mActionsListener.redeemNotification(clickedNotification);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        tvSensorValue.setText(detailedDevice.getStatus());
        tvSensorNotation.setText(detailedDevice.getStatusNotation());
        tvSensorProbeTitle.setText(detailedDevice.getDeviceProbeTitle());
        mActionsListener.openDevice(detailedDevice.getId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_devicedetail_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_devicedetail_custom_command:
                // Show dialog for custom command input
                showCustomCommandDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_devicedetail);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showMissingDevice() {
        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.devicedetail_empty_device_message),
                getView());
    }

    @Override
    public void showDeviceLoadError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }

    @Override
    public void showTitle(String title) {
        ((DeviceDetailActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public void showLocation(String location) {
        tvLocation.setText(location);
    }

    @Override
    public void hideLocation() {
        tvLocation.setText(getString(R.string.devicedetail_location_unknown));
    }

    @Override
    public void showState(final Device device) {
        detailedDevice = device;
        // Load notifications for the device.
        switch (device.getType()) {
            case DEVICE_TYPE_SWITCH_BINARY:
                boolean stateIsOn = DeviceStatusHelper.parseStatusMessageToBoolean(device.getStatus());
                switchBinary.setOnCheckedChangeListener(null);
                switchBinary.setChecked(stateIsOn);
                if (stateIsOn) {
                    tvSwitchStateOn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvSwitchStateOff.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                } else {
                    tvSwitchStateOff.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvSwitchStateOn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                }
                switchBinary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        mCommandListener.onCommand(device, DeviceStatusHelper.parseBooleanStatusToCommand(device.getType(), checked));
                    }
                });
                break;

            case DEVICE_SENSOR_MULTILEVEL:
                tvSensorValue.setText(device.getStatus());
                tvSensorNotation.setText(device.getStatusNotation());
                tvSensorProbeTitle.setText(device.getDeviceProbeTitle());
                break;

            case DEVICE_TYPE_SWITCH_MULTILEVEL:
                tvSwitchMultilevelValue.setText(device.getStatus());
                int multilevelValue = Integer.parseInt(device.getStatus());
                seekbarSwitchMultiLevel.setProgress(multilevelValue);
                switchMultilevel.setOnCheckedChangeListener(null);
                if (multilevelValue > 0) {
                    switchMultilevel.setChecked(true);
                } else {
                    switchMultilevel.setChecked(false);
                }

                switchMultilevel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        mCommandListener.onCommand(device, DeviceStatusHelper.parseBooleanStatusToCommand(device.getType(), checked));
                    }
                });

                seekbarSwitchMultiLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        tvSwitchMultilevelValue.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (seekBar.getProgress() > 0) {
                            mCommandListener.onCommand(device, DeviceStatusHelper.getSwitchMultilevelExactCommand(seekBar.getProgress()));
                            //  TODO: We might need to check switchMultiLevel here to turn on the device if its turned off.
                        } else {
                            switchMultilevel.setChecked(false);
                        }
                    }
                });

                break;

            case DEVICE_TYPE_BATTERY:
                String batteryValue = String.format("%s %s", device.getStatus(), device.getStatusNotation());
                int batteryValueInt = Integer.parseInt(device.getStatus());

                if (batteryValueInt < 20) {
                    ivBatteryIcon.setImageResource(R.drawable.ic_battery_0);
                } else if (batteryValueInt < 40 && batteryValueInt > 20) {
                    ivBatteryIcon.setImageResource(R.drawable.ic_battery_40);
                } else if (batteryValueInt < 60 && batteryValueInt > 40) {
                    ivBatteryIcon.setImageResource(R.drawable.ic_battery_60);
                } else if (batteryValueInt < 80 && batteryValueInt > 60) {
                    ivBatteryIcon.setImageResource(R.drawable.ic_battery_80);
                }

                tvBatteryValue.setText(batteryValue);
                break;

            case DEVICE_SENSOR_THERMOSTAT:
                try {
                    tvThermostatValue.setText(String.format(Locale.getDefault(), "%.1f", Double.valueOf(device.getStatus())));
                    tvThermostatNotation.setText(device.getStatusNotation());

                    int currentProgress = ((int) Double.parseDouble(device.getStatus()) * 10) - (device.getDeviceMinValue() * 10);
                    seekbarThermostat.setMaxProcess((device.getDeviceMaxValue()) * 10 - (device.getDeviceMinValue() * 10));
                    seekbarThermostat.setCurProcess(currentProgress);

                    seekbarThermostat.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onChanged(CircleSeekBar circleSeekBar, int i) {
                            double thermostatValue = ((double) i / 10) + (device.getDeviceMinValue());
                            tvThermostatValue.setText(String.valueOf(thermostatValue));
                        }
                    });

                    seekbarThermostat.setOnTouchListener(new CircleSeekBar.OnTouchListener() {

                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                // User has released the seekbar. Send the thermostat value change command
                                seekbarThermostat.setOnSeekBarChangeListener(null);
                                mCommandListener.onCommand(device, DeviceStatusHelper.getThermostatCommand(Double.parseDouble(tvThermostatValue.getText().toString())));
                            }

                            return false;
                        }
                    });
                } catch (NumberFormatException ex) {
                    Log.d(LOG_TAG, String.format("Unable to parse thermostat value: %s", device.getStatus()));
                }
                break;
        }
        mActionsListener.loadNotificationsForDevice(device.getId());
    }

    @Override
    public void hideState() {

    }

    @Override
    public void showTags(String[] tags) {

    }

    @Override
    public void hideTags() {

    }

    @Override
    public void showDeviceControls(final String deviceType) {
        RelativeLayout controlLayout = getView().findViewById(R.id.devicedetail_controls_layout);

        switch (deviceType) {
            case DEVICE_TYPE_SWITCH_BINARY:
                if (controlLayout.indexOfChild(inflatedSwitchBinaryControlLayout) == -1) {
                    controlLayout.addView(inflatedSwitchBinaryControlLayout);
                }
                break;
            case DEVICE_SENSOR_MULTILEVEL:
                if (controlLayout.indexOfChild(inflatedSensorControlLayout) == -1) {
                    controlLayout.addView(inflatedSensorControlLayout);
                }
                break;
            case DEVICE_SENSOR_THERMOSTAT:
                if (controlLayout.indexOfChild(inflatedThermostatControlLayout) == -1) {
                    controlLayout.addView(inflatedThermostatControlLayout);
                }
                break;
            case DEVICE_TYPE_BATTERY:
                if (controlLayout.indexOfChild(inflatedBatteryControlLayout) == -1) {
                    controlLayout.addView(inflatedBatteryControlLayout);
                }
                break;
            case DEVICE_TYPE_SWITCH_MULTILEVEL:
                if (controlLayout.indexOfChild(inflatedSwitchMultilevelControlLayout) == -1) {
                    controlLayout.addView(inflatedSwitchMultilevelControlLayout);
                }
                break;
            default:
                Snackbar.make(getView(), getString(R.string.device_type_not_supported), Snackbar.LENGTH_SHORT).show();
                break;
        }
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

    private void delayedRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 1 second
                mActionsListener.openDevice(detailedDevice.getId());
                mActionsListener.loadNotificationsForDevice(detailedDevice.getId());
            }
        }, 3000);
    }

    @Override
    public void showCommandFailedMessage(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        delayedRefresh();
    }

    @Override
    public void updateFavoriteIndicator(final String deviceId) {

        Set<String> favoriteDevices = FavoritesHelper.getFavoriteDevicesFromPrefs(getContext());
        if (favoriteDevices.contains(deviceId)) {
            ibFavorite.setImageResource(R.drawable.ic_star_24dp);
            isFavorite = true;
        } else {
            ibFavorite.setImageResource(R.drawable.ic_star_border_24dp);
            isFavorite = false;
        }

        ibFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isFavorite) {
                    mActionsListener.makeNotFavorite(deviceId, getContext());
                } else {
                    mActionsListener.makeFavorite(deviceId, getContext());
                }
            }

        });
    }

    @Override
    public void showNotifications(List<Notification> notifications) {
        if (getView() != null) {

            RelativeLayout emptyEventsLayout = getView().findViewById(R.id.devicedetail_events_empty_layout);
            SwipeRefreshLayout eventsListLayout = getView().findViewById(R.id.refresh_layout_devicedetail);
            SwipeRefreshLayout multilevelEventsListLayout = getView().findViewById(R.id.refresh_layout_sensormultileveldetail);

            if (!notifications.isEmpty()) {
                emptyEventsLayout.setVisibility(View.GONE);
                // Show charts if device type is supported
                if (detailedDevice != null) {
                    if (detailedDevice.getType().equals(DeviceDataContract.DEVICE_SENSOR_MULTILEVEL)) {
                        multilevelEventsListLayout.setVisibility(View.VISIBLE);
                        showEventChart(notifications);
                    } else {
                        mListAdapter.replaceData(notifications);
                        eventsListLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                emptyEventsLayout.setVisibility(View.VISIBLE);
                eventsListLayout.setVisibility(View.GONE);
                multilevelEventsListLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showNotificationsLoadError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }

    @Override
    public void showNotificationRedeemed() {
        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.notification_redeemed), getView());
    }

    @Override
    public void showNotificationDeleted() {
        SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.notification_deleted), getView());
    }

    @Override
    public void showNotificationUpdateError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        mActionsListener.loadNotificationsForDevice(detailedDevice.getId());
    }

    @Override
    public void showCustomCommandDialog() {
        if (detailedDevice != null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_custom_command, null);
            alertDialog.setView(dialogView);
            TextView dialogSubtitle = dialogView.findViewById(R.id.custom_command_subtitle);
            final EditText commandInput = dialogView.findViewById(R.id.custom_command_input);
            dialogSubtitle.setText(detailedDevice.getId());
            /* When positive (yes/ok) is clicked */
            alertDialog.setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    String customCommandString = commandInput.getText().toString();
                    mCommandListener.onCommand(detailedDevice, customCommandString);
                }
            });

            /* When negative (No/cancel) button is clicked*/
            alertDialog.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); // Your custom code
                }
            });
            alertDialog.show();
        } else {
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.devicedetail_not_ready), getView());
        }

    }

    private void configureToolTip() {
        // Chart tooltip
        mTip = new Tooltip(getContext(), R.layout.item_tooltip, R.id.value);
        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(180), (int) Tools.fromDpToPx(25));
        mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

        mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

        mTip.setPivotX(Tools.fromDpToPx(65) / 2);
        mTip.setPivotY(Tools.fromDpToPx(25));
    }

    private void showEventChart(final List<Notification> notifications) {
        lineChart.dismissAllTooltips();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("E HH:mm", Locale.ENGLISH);

        if (getView() != null) {
            // Sort notifications by date.
            Collections.sort(notifications);
            List<Integer> dataSetList = new ArrayList<>();
            LineSet dataset = new LineSet();
            lineChart.reset();

            for (Notification notification : notifications) {
                String message = notification.getMessage();
                if (message != null && detailedDevice.getStatusNotation() != null) {
                    float value = Float.parseFloat(message.replace(detailedDevice.getStatusNotation(), ""));
                    String dateStr = dateFormat.format(notification.getTimestamp());
                    dataset.addPoint(dateStr, value);
                    dataSetList.add(Math.round(value));
                }

            }

            // Tooltip animation and presentation.
            if (!dataSetList.isEmpty()) {
                lineChart.addData(dataset);

                final int LINE_MAX = Collections.max(dataSetList) + 5;
                final int LINE_MIN = Collections.min(dataSetList) - 5;
                int stepSizeTemp = Collections.max(dataSetList) / 10;
                if (Collections.max(dataSetList) < 10) {
                    stepSizeTemp = 1;
                }
                final int STEP_SIZE = stepSizeTemp;

                // Style the data set
                dataset.setDotsColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                        .setDotsRadius(Tools.fromDpToPx(5))
                        .setDotsStrokeThickness(Tools.fromDpToPx(2))
                        .setDotsStrokeColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                        .setColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                        .setThickness(Tools.fromDpToPx(3))
                        .beginAt(0).endAt(dataSetList.size());


                lineChart.setXLabels(AxisRenderer.LabelPosition.NONE);

                lineChart.setBorderSpacing(4)
                        .setAxisBorderValues(LINE_MIN, LINE_MAX, STEP_SIZE)
                        .setXAxis(false)
                        .setYAxis(false);

                Animation anim = new Animation(500);
                anim.fromXY(0, .5f);
                anim.fromAlpha(0);
                anim.setInterpolator(new LinearInterpolator());

                lineChart.setOnEntryClickListener(new OnEntryClickListener() {

                    @Override
                    public void onClick(int setIndex, int entryIndex, Rect rect) {
                        lineChart.dismissAllTooltips();
                        TextView tooltipTv = mTip.findViewById(R.id.value);
                        mTip.prepare(rect, 0);
                        tooltipTv.setText(String.format("%s @ %s",
                                notifications.get(entryIndex).getMessage(),
                                dateFormat.format(notifications.get(entryIndex).getTimestamp())));
                        lineChart.showTooltip(mTip, true);
                    }
                });

                lineChart.setAxisLabelsSpacing(30);
                lineChart.show(anim);
            }
        }
    }


    /**
     * Listener for commands to devices.
     */
    private final DeviceCommandListener mCommandListener = new DeviceCommandListener() {
        @Override
        public void onCommand(Device commandedDevice, String command) {
            mActionsListener.sendCommand(commandedDevice, command);
        }

    };
}