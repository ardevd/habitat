package no.aegisdynamics.habitat.setup;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import no.aegisdynamics.habitat.data.device.DevicesRepository;

import static org.mockito.Mockito.verify;

public class SetupPresenterTest {

    @Mock
    private DevicesRepository mDevicesRepository;

    @Mock
    private SetupContract.View mSetupView;

    private SetupPresenter mSetupPresenter;

    @Captor
    private ArgumentCaptor<DevicesRepository.GetControllerStatusCallback> mGetControllerForSetupCallbackCaptor;

    @Before
    public void setupSetupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mSetupPresenter = new SetupPresenter(mDevicesRepository, mSetupView);
    }

    @Test
    public void saveHostname_showsEmptyHostname() {
        // When the presenter is asked to verify the hostname with an empty hostname
        mSetupPresenter.verifyHostname("");
        // An error is returned telling the user to enter a valid hostname
        verify(mSetupView).showEmptyHostname();
    }

    @Test
    public void saveHostname_showsInvalidHost() {
        // When the presenter is asked to verify the hostname...
        mSetupPresenter.verifyHostname("zway.mydomain.com");
        verify(mSetupView).setProgressIndicator(true);
        verify(mDevicesRepository).getControllerStatus(mGetControllerForSetupCallbackCaptor.capture());
        // And is unable to connect...
        mGetControllerForSetupCallbackCaptor.getValue().onControllerDown("Error");
        // An error is returned to the user.
        verify(mSetupView).setProgressIndicator(false);
        verify(mSetupView).showInvalidHostname("Error");
    }

    @Test
    public void saveHostname_showsValidAnonymousHost() {
        // When the presenter is asked to verify the hostname...
        mSetupPresenter.verifyHostname("zway.mydomain.com");
        verify(mSetupView).setProgressIndicator(true);
        verify(mDevicesRepository).getControllerStatus(mGetControllerForSetupCallbackCaptor.capture());
        // And connects successfully without credentials...
        mGetControllerForSetupCallbackCaptor.getValue().onControllerUp();
        // The custom name view is shown the user.
        verify(mSetupView).setProgressIndicator(false);
        verify(mSetupView).showHostnameAnonymousAccessApproved();
    }

    @Test
    public void saveCredentials_showInvalidCredentials() {
        // When the presenter is asked to verify credentials...
        mSetupPresenter.verifyCredentials();
        verify(mSetupView).setProgressIndicator(true);
        verify(mDevicesRepository).getControllerStatus(mGetControllerForSetupCallbackCaptor.capture());
        // And fails...
        mGetControllerForSetupCallbackCaptor.getValue().onControllerDown("Authentication Error");
        // The credentials view is shown the user.
        verify(mSetupView).setProgressIndicator(false);
        verify(mSetupView).showInvalidCredentials("Authentication Error");
    }

    @Test
    public void saveCredentials_showValidCredentials() {
        // When the presenter is asked to verify credentials...
        mSetupPresenter.verifyCredentials();
        verify(mSetupView).setProgressIndicator(true);
        verify(mDevicesRepository).getControllerStatus(mGetControllerForSetupCallbackCaptor.capture());
        // And authentication is successful.
        mGetControllerForSetupCallbackCaptor.getValue().onControllerUp();
        // The valid credentials to the user.
        verify(mSetupView).setProgressIndicator(false);
        verify(mSetupView).showValidCredentials();
    }

    @Test
    public void saveSetup_emptyName() {
        // When the presenter is asked to verify custom name...
        mSetupPresenter.saveSetupData("");
        // The user is told to enter a name.
        verify(mSetupView).showEmptyCustomName();
    }

    @Test
    public void saveSetup_validSetup() {
        // When the presenter is asked to custom name...
        mSetupPresenter.saveSetupData("Name");
        verify(mSetupView).setProgressIndicator(true);
        verify(mDevicesRepository).getControllerStatus(mGetControllerForSetupCallbackCaptor.capture());
        // And the check is successful.
        mGetControllerForSetupCallbackCaptor.getValue().onControllerUp();
        // The valid credentials to the user.
        verify(mSetupView).setProgressIndicator(false);
        verify(mSetupView).showValidSetup();
    }

    @Test
    public void saveSetup_invalidSetup() {
        // When the presenter is asked to custom name...
        mSetupPresenter.saveSetupData("Name");
        verify(mSetupView).setProgressIndicator(true);
        verify(mDevicesRepository).getControllerStatus(mGetControllerForSetupCallbackCaptor.capture());
        // And authentication is successful.
        mGetControllerForSetupCallbackCaptor.getValue().onControllerDown("Some Error");
        // The valid credentials to the user.
        verify(mSetupView).setProgressIndicator(false);
        verify(mSetupView).showInvalidSetup("Some Error");
    }

}
