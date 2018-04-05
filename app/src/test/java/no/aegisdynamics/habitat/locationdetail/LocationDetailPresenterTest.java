package no.aegisdynamics.habitat.locationdetail;


import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.data.location.LocationsRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class LocationDetailPresenterTest {

    // Dummy location for testing
    private static Location LOCATION = new Location(1, "Office", "office.jpg", 2);

    private static List<Device> DEVICES = Lists.newArrayList(new Device("test-device-01", "Test Device", "switchBinary", "Office", 1, 1, null, "on", 0, 0, null, null, "icon"),
            new Device("test-device-02", "Test Device 02", "doorlock", "Living Room", 2, 2, null, "close",0 , 0, null, null, "icon"));

    @Mock
    private DevicesRepository mDevicesRepository;

    @Mock
    private LocationDetailContract.View mLocationDetailView;

    @Mock
    private LocationsRepository mLocationsRepository;

    @Captor
    private ArgumentCaptor<LocationsRepository.GetLocationCallback> mGetLocationCallbackCaptor;

    @Captor
    private ArgumentCaptor<DevicesRepository.GetDevicesForLocationCallback> mGetDeviceForLocationCallbackCaptor;

    @Captor
    private ArgumentCaptor<LocationsRepository.UpdateLocationCallback> mUpdateLocationCallbackCaptor;

    private LocationDetailPresenter mLocationDetailPresenter;

    @Before
    public void setupDevicePresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mLocationDetailPresenter = new LocationDetailPresenter(mDevicesRepository, mLocationsRepository, mLocationDetailView);
    }

    @Test
    public void getLocationFromRepositoryAndLoadIntoView() {
        mLocationDetailPresenter.openLocation(LOCATION.getId());

        // Then device is loaded from model, callback is captured and progress indicator is shown
        verify(mLocationsRepository).getLocation(eq(LOCATION.getId()), mGetLocationCallbackCaptor.capture());
        verify(mLocationDetailView).setProgressIndicator(true);
        // When device is finally loaded
        mGetLocationCallbackCaptor.getValue().onLocationLoaded(LOCATION); // Trigger callback

        // Then progress indicator is hidden and title, destination, description, start/end time is shown in UI.
        verify(mLocationDetailView).setProgressIndicator(false);
        verify(mLocationDetailView).showTitle(LOCATION.getTitle());
    }

    @Test
    public void getLocationFromRepositoryFailureAndShowError() {
        mLocationDetailPresenter.openLocation(LOCATION.getId());

        // Then device is loaded from model, callback is captured and progress indicator is shown
        verify(mLocationsRepository).getLocation(eq(LOCATION.getId()), mGetLocationCallbackCaptor.capture());
        verify(mLocationDetailView).setProgressIndicator(true);
        // When location fails to load
        mGetLocationCallbackCaptor.getValue().onLocationLoadError("Error");

        // Then progress indicator is hidden and title, destination, description, start/end time is shown in UI.
        verify(mLocationDetailView).setProgressIndicator(false);
        verify(mLocationDetailView).showLocationLoadError("Error");
    }

    @Test
    public void getDevicesFromRepositoryAndLoadIntoView() {
        mLocationDetailPresenter.loadDevicesForLocation(LOCATION.getId());

        verify(mDevicesRepository).getDevicesForLocation(eq(LOCATION.getId()), mGetDeviceForLocationCallbackCaptor.capture());
        verify(mLocationDetailView).setProgressIndicator(true);

        mGetDeviceForLocationCallbackCaptor.getValue().onDevicesForLocationLoaded(DEVICES);

        // Progress indicator is hidden and devices are shown in the UI
        verify(mLocationDetailView).setProgressIndicator(false);
        verify(mLocationDetailView).showDevices(DEVICES);
    }

    @Test
    public void editLocation() {
        mLocationDetailPresenter.editLocation(LOCATION.getId(), "Test title");

        verify(mLocationsRepository).updateLocation(eq(LOCATION.getId()), eq("Test title"), mUpdateLocationCallbackCaptor.capture());
        verify(mLocationDetailView).setProgressIndicator(true);

        mUpdateLocationCallbackCaptor.getValue().onLocationUpdated();

        verify(mLocationDetailView).setProgressIndicator(false);
        verify(mLocationDetailView).showLocationUpdated();

    }

}