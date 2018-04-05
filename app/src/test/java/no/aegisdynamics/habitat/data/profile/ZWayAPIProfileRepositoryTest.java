package no.aegisdynamics.habitat.data.profile;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the ZWayAPIProfileRepository
 */
public class ZWayAPIProfileRepositoryTest {

    private ZWayAPIProfileRepository mProfileRepository;

    private static Profile PROFILE = new Profile(1, "user", "User", "user@domain.com",
            new ArrayList<String>());

    @Mock
    private ProfileServiceApi serviceApi;

    @Mock
    private Context context;

    @Mock
    private ProfileRepository.GetProfileCallback mockedGetProfileCallback;

    @Captor
    private ArgumentCaptor<ProfileServiceApi.ProfileServiceCallback> callbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mProfileRepository = new ZWayAPIProfileRepository(serviceApi, context);
    }

    @Test
    public void getProfile_returnData() {
        mProfileRepository.getProfile(PROFILE.getUserName(), mockedGetProfileCallback);
        verify(serviceApi).getProfile(eq(context), eq(PROFILE.getUserName()), callbackCaptor.capture());
        callbackCaptor.getValue().onLoaded(PROFILE);
        verify(mockedGetProfileCallback).onProfileLoaded(eq(PROFILE));
    }

    @Test
    public void getProfile_returnError() {
        mProfileRepository.getProfile(PROFILE.getUserName(), mockedGetProfileCallback);
        verify(serviceApi).getProfile(eq(context), eq(PROFILE.getUserName()), callbackCaptor.capture());
        callbackCaptor.getValue().onError("error");
        verify(mockedGetProfileCallback).onProfileLoadError(eq("error"));
    }


}
