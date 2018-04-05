package no.aegisdynamics.habitat.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.R;
import no.aegisdynamics.habitat.adapters.DevicesAdapter;
import no.aegisdynamics.habitat.data.Injection;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.weather.Weather;
import no.aegisdynamics.habitat.devicedetail.DeviceDetailActivity;
import no.aegisdynamics.habitat.dialogs.HabitatAppModuleInfoDialog;
import no.aegisdynamics.habitat.itemListeners.DeviceCommandListener;
import no.aegisdynamics.habitat.itemListeners.DeviceItemListener;
import no.aegisdynamics.habitat.util.GooglePlayServicesHelper;
import no.aegisdynamics.habitat.util.SnackbarHelper;
import no.aegisdynamics.habitat.util.TemperatureConverterHelper;
import no.aegisdynamics.habitat.util.UserCredentialsManager;
import no.aegisdynamics.habitat.util.WeatherIconFontHelper;

/**
 * Fragment for the Dashboard screen
 */

public class DashboardFragment extends Fragment implements DashboardContract.View {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private DashboardContract.UserActionsListener mUserActionsListener;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private TextView temperatureTextView;
    private RelativeLayout weatherLayout;
    private LinearLayout placeLayout;

    private DevicesAdapter mListAdapter;

    final private static int PLACE_PICKER_REQUEST = 1;

    private boolean locationHintShown = false;

    private View lockStateLayout;

    // List of zway device IDs that are assigned to the user
    private List<String> mDashboardDevices;

    // Boolean to track whether the user has dismissed lock state warnings
    private boolean userDismissedLockWarning = false;

    public DashboardFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getContext());
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext());

        mListAdapter = new DevicesAdapter(new ArrayList<Device>(0), mItemListener, mCommandListener);

        // Construct a DashboardPresenter.
        mUserActionsListener = new DashboardPresenter(this,
                Injection.provideWeatherRepository(getContext()),
                Injection.provideDevicesRepository(getContext()),
                Injection.provideProfileRepository(getContext()));

        // We have a menu!
        setHasOptionsMenu(true);
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        callGetWeatherData();

        if (GooglePlayServicesHelper.checkForGooglePlayServices(getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST) && FirebaseInstanceId.getInstance().getToken() != null) {
            mUserActionsListener.registerFCMDeviceToken(FirebaseInstanceId.getInstance().getToken());
        }

        mUserActionsListener.getProfileData(UserCredentialsManager.getZwayUsername(getContext().getApplicationContext()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_dashboard_push_notifications:
                showAppSupportInfoDialog();
                break;
            case R.id.menu_dashboard_location:
                // Show location picker
                showPlacePicker();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void callGetWeatherData() {
        try {
            placeLayout.setVisibility(View.GONE);
            weatherLayout.setVisibility(View.VISIBLE);
            double lat = Double.valueOf(settings.getString("home_lat", ""));
            double lon = Double.valueOf(settings.getString("home_long", ""));
            mUserActionsListener.getWeatherData(lat, lon);
        } catch (NumberFormatException ex) {
            weatherLayout.setVisibility(View.GONE);
            placeLayout.setVisibility(View.VISIBLE);
            if (!settings.getBoolean("dashboard_location_hint_shown", false)) {
                ((DashboardActivity) getActivity()).showLocationAddHint();
                editor.putBoolean("dashboard_location_hint_shown", true).apply();
                locationHintShown = true;
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout_dashboard);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUserActionsListener.loadDevices();
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.dashboard_list);
        recyclerView.setAdapter(mListAdapter);
        int numColumns = getContext().getResources().getInteger(R.integer.num_dashboard_columns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        Button locationButton = root.findViewById(R.id.dashboard_location_button);
        temperatureTextView = root.findViewById(R.id.dashboard_weather_temperature);
        weatherLayout = root.findViewById(R.id.dashboard_weather_layout);
        placeLayout = root.findViewById(R.id.dashboard_empty_place_layout);
        TextView homeTitleTextView = root.findViewById(R.id.dashboard_home_title);
        settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = settings.edit();
        homeTitleTextView.setText(settings.getString("zway_name", ""));
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker();
            }
        });

        Animation animation;
        animation = AnimationUtils.loadAnimation(getContext().getApplicationContext(),
                R.anim.move);
        homeTitleTextView.startAnimation(animation);

        // Dynamic views
        LayoutInflater dynamicInflater = LayoutInflater.from(getActivity());
        lockStateLayout = dynamicInflater.inflate(R.layout.dashboard_lock_state, container, false);

        return root;
    }

    @Override
    public void showPlacePicker() {

        if (GooglePlayServicesHelper.checkForGooglePlayServices(getActivity(), PLACE_PICKER_REQUEST)) {
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void showWeatherData(Weather weather) {
        try {
            View view = getView();
            if (view != null) {
                double temperature;
                double temperatureMax;
                double temperatureMin;

                switch (settings.getString("weather_unit", getString(R.string.prefs_temperature_units_celsius))) {
                    case "Celsius":
                        temperature = TemperatureConverterHelper.convertKelvinToCelsius(weather.getTemperature());
                        temperatureMax = TemperatureConverterHelper.convertKelvinToCelsius(weather.getTemperatureMax());
                        temperatureMin = TemperatureConverterHelper.convertKelvinToCelsius(weather.getTemperatureMin());
                        break;
                    case "Fahrenheit":
                        temperature = TemperatureConverterHelper.convertKelvinToFahrenheit(weather.getTemperature());
                        temperatureMax = TemperatureConverterHelper.convertKelvinToFahrenheit(weather.getTemperatureMax());
                        temperatureMin = TemperatureConverterHelper.convertKelvinToFahrenheit(weather.getTemperatureMin());
                        break;
                    default:
                        temperature = weather.getTemperature();
                        temperatureMax = weather.getTemperatureMax();
                        temperatureMin = weather.getTemperatureMin();
                }

                // Round up temperature values to two decimals.
                temperature = Math.round(temperature * 100) / 100;
                temperatureMax = Math.round(temperatureMax * 100) / 100;
                temperatureMin = Math.round(temperatureMin * 100) / 100;

                temperatureTextView.setText(String.format("%s%s", String.valueOf(temperature), "\u00B0"));
                TextView temperatureMaxView = getView().findViewById(R.id.dashboard_weather_temperature_max);
                TextView temperatureMinView = getView().findViewById(R.id.dashboard_weather_temperature_min);
                TextView weatherConditionView = getView().findViewById(R.id.dashboard_weather_condition);
                TextView weatherIconView = getView().findViewById(R.id.dashboard_weather_icon);

                Typeface face = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
                weatherIconView.setTypeface(face);
                weatherIconView.setText(WeatherIconFontHelper.parseConditionIconToFontString(weather.getConditionIcon()));

                weatherConditionView.setText(weather.getCondition());

                temperatureMaxView.setText(String.format("%s%s", String.valueOf(temperatureMax), "\u00B0"));
                temperatureMinView.setText(String.format("%s%s", String.valueOf(temperatureMin), "\u00B0"));
            }
        } catch (IllegalStateException ex) {
            // Illegal state.
            ex.printStackTrace();
        }
    }

    @Override
    public void showWeatherDataError(String error) {

    }

    @Override
    public void showDevices(List<Device> devices) {
        DashboardDeviceLoaderTask loaderTask = new DashboardDeviceLoaderTask();
        loaderTask.execute(devices);
    }

    @Override
    public void showDevicesLoadError(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout_dashboard);
        // Make sure setRefreshing() is called after layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showDeviceDetailUI(@NonNull Device device) {
        // in it's own Activity, since it makes more sense that
        Intent intent = new Intent(getContext(), DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_ID, device.getId());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_TITLE, device.getTitle());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_STATE, device.getStatus());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_NOTATION, device.getStatusNotation());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_PROBE_TITLE, device.getDeviceProbeTitle());
        startActivity(intent);
    }


    @Override
    public void showCommandFailedMessage(String error) {
        SnackbarHelper.showSimpleSnackbarMessage(error, getView());
        delayedRefresh();
    }

    @Override
    public void showCommandSuccessMessage() {
        String statusMessage = getString(R.string.devices_command_ok);

        SnackbarHelper.showSimpleSnackbarMessage(statusMessage, getView());
        delayedRefresh();
    }

    @Override
    public void showDeviceTokenRegistrationError(String error) {
        boolean appModuleInfo = settings.getBoolean("app_module_notification_shown", false);
        if (!appModuleInfo) {
            editor.putBoolean("app_module_notification_shown", true).apply();
            editor.putBoolean("token_registered", false).apply();
            showAppSupportInfoDialog();
        }
    }

    @Override
    public void showDeviceTokenRegistered() {
        boolean tokenRegistered = settings.getBoolean("token_registered", false);
        if (!tokenRegistered) {
            editor.putBoolean("token_registered", true).apply();
            SnackbarHelper.showSimpleSnackbarMessage(getString(R.string.fcm_token_registered_message), getView());
        }
    }

    @Override
    public void showLockStateWarning(int numberOfLocksOpen) {
        if (!userDismissedLockWarning) {
            View view = getView();
            if (view != null) {
                final LinearLayout dynamicLayout = getView().findViewById(R.id.dashboard_dynamic_states);
                if (dynamicLayout.indexOfChild(lockStateLayout) == -1) {
                    TextView lockWarningText = lockStateLayout.findViewById(R.id.dashboard_security_state_description);
                    if (numberOfLocksOpen == 1) {
                        lockWarningText.setText(R.string.dashboard_warning_lock_open);
                    } else {
                        lockWarningText.setText(getString(R.string.dashboard_warning_locks_open, numberOfLocksOpen));
                    }
                    dynamicLayout.addView(lockStateLayout);

                    // Swipe to dismiss
                    //<V> - The View type that this Behavior operates on
                    final CardView lockStateCardView = lockStateLayout.findViewById((R.id.dashboard_lock_state_card));
                    SwipeDismissBehavior<CardView> swipeDismissBehavior = new SwipeDismissBehavior();
                    swipeDismissBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY);
                    swipeDismissBehavior.setListener(new SwipeDismissBehavior.OnDismissListener() {

                        @Override
                        public void onDismiss(View view) {
                            // View was dismissed. Remove it from the parent.
                            userDismissedLockWarning = true;
                            dynamicLayout.removeView(lockStateLayout);
                        }

                        @Override
                        public void onDragStateChanged(int state) {

                        }
                    });

                    CoordinatorLayout.LayoutParams layoutParams =
                            (CoordinatorLayout.LayoutParams) lockStateCardView.getLayoutParams();
                    layoutParams.setBehavior(swipeDismissBehavior);
                }
            }
        }
    }

    @Override
    public void hideLockStateWarning() {
        View view = getView();
        if (view != null) {
            LinearLayout dynamicLayout = getView().findViewById(R.id.dashboard_dynamic_states);
            dynamicLayout.removeView(lockStateLayout);
        }
    }

    @Override
    public void showAppSupportInfoDialog() {
        HabitatAppModuleInfoDialog dialog = new HabitatAppModuleInfoDialog();
        FragmentManager manager = getFragmentManager();
        dialog.show(manager, "habitat_app_module_dialog");
    }

    @Override
    public void onDashboardDevicesRetrieved(List<String> dashboardDevices) {
        mDashboardDevices = dashboardDevices;
        // dashboard devices have been retrieved. Get all devices.
        mUserActionsListener.loadDevices();
    }

    private void delayedRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 2 seconds
                mUserActionsListener.loadDevices();
            }
        }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                editor.putString("home_lat", String.valueOf(latitude));
                editor.putString("home_long", String.valueOf(longitude));
                editor.apply();
                callGetWeatherData();
            }
        }
    }

    /**
     * Listener for clicks on devices in the RecyclerView.
     */
    private final DeviceItemListener mItemListener = new DeviceItemListener() {

        @Override
        public void onDeviceClick(Device clickedDevice) {
            mUserActionsListener.openDeviceDetails(clickedDevice);
        }
    };

    /**
     * Listener for commands to devices.
     */
    private final DeviceCommandListener mCommandListener = new DeviceCommandListener() {
        @Override
        public void onCommand(Device commandedDevice, String command) {
            mUserActionsListener.sendCommand(commandedDevice, command);
        }
    };

    private class DashboardDeviceLoaderTask extends AsyncTask<List<Device>, Integer, List<Device>> {

        @Override
        protected List<Device> doInBackground(List<Device>[] deviceList ) {
            List<Device> dashDevicesList = new ArrayList<>();
            for (Device device: deviceList[0]) {
                if (mDashboardDevices.contains(device.getId())) {
                        dashDevicesList.add(device);
                }

            }
            return dashDevicesList;
        }

        @Override
        protected void onPostExecute(List<Device> devices) {
            try {
                if (devices.size() > 0) {
                    getView().findViewById(R.id.dashboard_no_devices_layout).setVisibility(View.GONE);
                } else {
                    getView().findViewById(R.id.dashboard_no_devices_layout).setVisibility(View.VISIBLE);
                    if (!settings.getBoolean("dashboard_devices_hint_shown", false) &&
                            settings.getBoolean("dashboard_location_hint_shown", false) &&
                            !locationHintShown) {
                        ((DashboardActivity) getActivity()).showDevicesHint();
                        editor.putBoolean("dashboard_devices_hint_shown", true).apply();
                    }

                }
                // Replace adapter data with the new list of devices
                mListAdapter.replaceData(devices);
            } catch (NullPointerException ex) {
                // We were probably unable to get a view.
                ex.printStackTrace();
            }
        }
    }
}
