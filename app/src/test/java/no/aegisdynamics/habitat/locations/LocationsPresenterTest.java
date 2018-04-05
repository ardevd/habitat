package no.aegisdynamics.habitat.locations;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.data.location.LocationsRepository;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class LocationsPresenterTest {

    // List with dummy devices for testing
    private static List<Location> LOCATIONS = Lists.newArrayList(new Location(1, "Office", "office.jpg", 2),
            new Location(2, "Balcony", "balcony.jpg", 0));

    @Mock
    private LocationsRepository mLocationsRepository;

    @Mock
    private LocationsContract.View mLocationsView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<LocationsRepository.LoadLocationsCallback> mLoadLocationsCallbackCaptor;

    @Captor
    private ArgumentCaptor<LocationsRepository.DeleteLocationCallback> mDeleteLocationCallbackCaptor;

    private LocationsPresenter mLocationsPresenter;

    @Before
    public void setupLocationPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mLocationsPresenter = new LocationsPresenter(mLocationsRepository, mLocationsView);
    }

    @Test
    public void loadLocationsFromRepositoryAndLoadIntoView() {
        // Given an initialized LocationsPresenter with initialized devices
        // When loading of devices is requested
        mLocationsPresenter.loadLocations();

        // Callback is captured and invoked with stubbed locations
        verify(mLocationsView).setProgressIndicator(true);
        verify(mLocationsRepository).getLocations(mLoadLocationsCallbackCaptor.capture());
        mLoadLocationsCallbackCaptor.getValue().onLocationsLoaded(LOCATIONS);

        // Progress indicator is hidden and locations are shown in the UI
        verify(mLocationsView).setProgressIndicator(false);
        verify(mLocationsView).showLocations(LOCATIONS);
    }

    @Test
    public void loadLocationsFromRepositoryFailureAndErrorMessageShown() {
        // Given an initialized LocationsPresenter with initialized devices
        // When loading of devices is requested
        mLocationsPresenter.loadLocations();

        // Callback is captured and invoked with stubbed locations
        verify(mLocationsView).setProgressIndicator(true);
        verify(mLocationsRepository).getLocations(mLoadLocationsCallbackCaptor.capture());
        mLoadLocationsCallbackCaptor.getValue().onLocationsLoadError("Location load error!");

        // Progress indicator is hidden and error message is shown in the UI
        verify(mLocationsView).setProgressIndicator(false);
        verify(mLocationsView).showLocationsLoadError("Location load error!");
    }

    @Test
    public void clickOnLocation_ShowsDetailUi() {
        // Given a stubbed Location
        Location requestedLocation = new Location(3, "Master Bed Room", "master_bed_room.jpg", 3);

        // When open location is requested
        mLocationsPresenter.openLocationDetails(requestedLocation);

        // Then location detail UI is shown
        verify(mLocationsView).showLocationDetailUI(any(Integer.class), any(String.class));
    }

    @Test
    public void clickOnFab_ShowsAddLocationUI() {
        // When adding a new location
        mLocationsPresenter.addLocation();
        // Then add Locations UI is shown
        verify(mLocationsView).showAddLocation();
    }

    @Test
    public void deleteLocation() {
        mLocationsPresenter.deleteLocation(LOCATIONS.get(1));

        verify(mLocationsView).setProgressIndicator(true);
        verify(mLocationsRepository).deleteLocation(eq(LOCATIONS.get(1).getId()), mDeleteLocationCallbackCaptor.capture());
        mDeleteLocationCallbackCaptor.getValue().onLocationDeleted();

        // Message is shown in the UI
        verify(mLocationsView).setProgressIndicator(false);
        verify(mLocationsView).showDeleteLocation();
    }

    @Test
    public void deleteLocationError() {
        mLocationsPresenter.deleteLocation(LOCATIONS.get(1));

        verify(mLocationsView).setProgressIndicator(true);
        verify(mLocationsRepository).deleteLocation(eq(LOCATIONS.get(1).getId()), mDeleteLocationCallbackCaptor.capture());
        mDeleteLocationCallbackCaptor.getValue().onLocationDeleteError("Error");

        // Error message is shown in the UI
        verify(mLocationsView).setProgressIndicator(false);
        verify(mLocationsView).showDeleteLocationError("Error");
    }


}