package no.aegisdynamics.habitat.locationadd;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

import no.aegisdynamics.habitat.data.location.Location;
import no.aegisdynamics.habitat.data.location.LocationsRepository;


public class LocationAddPresenterTest {

    private static Location LOCATION = new Location(0, "Room Name", null, 0);

    @Mock
    private LocationsRepository mLocationsRepository;

    @Mock
    private LocationAddContract.View mLocationAddView;

    private LocationAddPresenter mLocationAddPresenter;

    @Captor
    private ArgumentCaptor<LocationsRepository.CreateLocationCallback> mCreateLocationCallbackCaptor;

    @Before
    public void setupLocationAddPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mLocationAddPresenter = new LocationAddPresenter(mLocationsRepository, mLocationAddView);
    }

    @Test
    public void saveLocationToRepository_showsSuccessMessageUi() {
        // When the presenter is asked to save a new Location

        mLocationAddPresenter.saveLocation(LOCATION);

        // Then a Location is,
        verify(mLocationsRepository).createLocation(any(Location.class), mCreateLocationCallbackCaptor.capture());
        mCreateLocationCallbackCaptor.getValue().onLocationCreated();
        verify(mLocationAddView).showLocationAdded(); // shown in the UI.
    }
}
