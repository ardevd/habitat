package no.aegisdynamics.habitat.dashboard;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.profile.Profile;
import no.aegisdynamics.habitat.data.profile.ProfileRepository;
import no.aegisdynamics.habitat.data.weather.WeatherRepository;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the Dashboard presenter
 */

public class DashboardPresenterTest {

    // List with dummy devices for testing
    private static List<Device> DEVICES = Lists.newArrayList(new Device("test-device-01", "Test Device", "switchBinary", "Office", 1, 1, null, "on", 0, 0, null, null, null),
            new Device("test-device-02", "Test Device 02", "doorlock", "Living Room", 2, 2, null, "close",0 , 0, null, null, "icon"));

    @Mock
    private DevicesRepository mDevicesRepository;
    @Mock
    private WeatherRepository mWeatherRepository;
    @Mock
    private ProfileRepository mProfileRepository;
    @Mock
    private DashboardContract.View mDashboardView;

    @Captor
    private ArgumentCaptor<DevicesRepository.RegisterFCMDeviceTokenCallback> mRegisterFCMDeviceTokenCallbackCaptor;

    @Captor
    private ArgumentCaptor<DevicesRepository.LoadDevicesCallback> mLoadDevicesCallbackCaptor;

    @Captor
    private ArgumentCaptor<ProfileRepository.GetProfileCallback> mGetProfileCallbackCaptor;

    private DashboardPresenter mDashboardPresenter;

    @Before
    public void setupDashboardPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mDashboardPresenter = new DashboardPresenter(mDashboardView, mWeatherRepository,
                mDevicesRepository, mProfileRepository);
    }

    @Test
    public void testFcmDeviceTokenRegistrationSuccess() {
        mDashboardPresenter.registerFCMDeviceToken("abc");
        verify(mDevicesRepository).registerFCMDeviceToken(any(String.class), mRegisterFCMDeviceTokenCallbackCaptor.capture());
        mRegisterFCMDeviceTokenCallbackCaptor.getValue().onTokenRegistered();
        verify(mDashboardView).showDeviceTokenRegistered();
    }

    @Test
    public void testFcmDeviceTokenRegistrationError() {
        mDashboardPresenter.registerFCMDeviceToken("abc");
        verify(mDevicesRepository).registerFCMDeviceToken(any(String.class), mRegisterFCMDeviceTokenCallbackCaptor.capture());
        mRegisterFCMDeviceTokenCallbackCaptor.getValue().onTokenRegisterError("error");
        verify(mDashboardView).showDeviceTokenRegistrationError("error");
    }

    @Test
    public void loadDevicesFromRepositoryAndLoadIntoView() {
        // Given an initialized DevicesPresenter with initialized devices
        // When loading of devices is requested
        mDashboardPresenter.loadDevices();

        // Callback is captured and invoked with stubbed devices
        verify(mDashboardView).setProgressIndicator(true);
        verify(mDevicesRepository).getDevices(mLoadDevicesCallbackCaptor.capture());
        mLoadDevicesCallbackCaptor.getValue().onDevicesLoaded(DEVICES);

        // Progress indicator is hidden and devices are shown in the UI
        verify(mDashboardView).setProgressIndicator(false);
        verify(mDashboardView).showDevices(DEVICES);
    }

    @Test
    public void loadDevicesFromRepositoryFailureAndShowError() {
        // Given an initialized DevicesPresenter with initialized devices
        // When loading of devices is requested
        mDashboardPresenter.loadDevices();

        // Callback is captured and invoked with stubbed devices
        verify(mDashboardView).setProgressIndicator(true);
        verify(mDevicesRepository).getDevices(mLoadDevicesCallbackCaptor.capture());
        mLoadDevicesCallbackCaptor.getValue().onDevicesLoadError("Test error");

        // Progress indicator is hidden and error message is shown in the UI
        verify(mDashboardView).setProgressIndicator(false);
        verify(mDashboardView).showDevicesLoadError(eq("Test error"));
    }

    @Test
    public void loadUserProfileData() {
        List<String> dashboardDevices = new ArrayList<>();
        // when loading profile is requested
        mDashboardPresenter.getProfileData("user");

        // Callback is captured and invoked with a stubbed Profile
        verify(mDashboardView).setProgressIndicator(true);
        verify(mProfileRepository).getProfile(any(String.class), mGetProfileCallbackCaptor.capture());
        mGetProfileCallbackCaptor.getValue().onProfileLoaded(new Profile(0, "admin",
                "admin@mail.com", dashboardDevices));

        verify(mDashboardView).onDashboardDevicesRetrieved(dashboardDevices);

    }
}
