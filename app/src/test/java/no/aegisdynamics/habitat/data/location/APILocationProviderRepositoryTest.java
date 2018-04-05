package no.aegisdynamics.habitat.data.location;

import android.content.Context;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by eho on 3/27/18.
 */

public class APILocationProviderRepositoryTest {

    private APILocationProviderRepository mLocationRepository;

    private static List<Location> LOCATIONS = Lists.newArrayList(new Location(1, "Office", "office.jpg", 2),
            new Location(2, "Balcony", "balcony.jpg", 0));


    @Mock
    private LocationsServiceApi serviceApi;

    @Mock
    private Context context;

    @Mock
    private LocationsRepository.LoadLocationsCallback mockedLoadLocationsCallback;

    @Mock
    private LocationsRepository.GetLocationCallback mockedGetLocationCallback;

    @Captor
    private ArgumentCaptor<LocationsServiceApi.LocationsServiceCallback> callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mLocationRepository = new APILocationProviderRepository(serviceApi, context);
    }

    @Test
    public void getLocations_returnData() {
        mLocationRepository.getLocations(mockedLoadLocationsCallback);
        verify(serviceApi).getAllLocations(eq(context), callback.capture());
        callback.getValue().onLoaded(LOCATIONS);
        verify(mockedLoadLocationsCallback).onLocationsLoaded(eq(LOCATIONS));
    }

    @Test
    public void getLocations_returnError() {
        mLocationRepository.getLocations(mockedLoadLocationsCallback);
        verify(serviceApi).getAllLocations(eq(context), callback.capture());
        callback.getValue().onError("Error");
        verify(mockedLoadLocationsCallback).onLocationsLoadError(eq("Error"));
    }

    @Test
    public void getLocation_returnData() {
        mLocationRepository.getLocation(LOCATIONS.get(0).getId(), mockedGetLocationCallback);
        verify(serviceApi).getLocation(eq(context), eq(LOCATIONS.get(0).getId()), callback.capture());
        callback.getValue().onLoaded(LOCATIONS.get(0));
        verify(mockedGetLocationCallback).onLocationLoaded(eq(LOCATIONS.get(0)));
    }

    @Test
    public void getLocation_returnError() {
        mLocationRepository.getLocation(LOCATIONS.get(0).getId(), mockedGetLocationCallback);
        verify(serviceApi).getLocation(eq(context), eq(LOCATIONS.get(0).getId()), callback.capture());
        callback.getValue().onError("Error");
        verify(mockedGetLocationCallback).onLocationLoadError(eq("Error"));
    }
}
