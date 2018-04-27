package no.aegisdynamics.habitat.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import no.aegisdynamics.habitat.data.device.Controller;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the Controller Presenter
 */

public class ControllerPresenterTest implements DeviceDataContract {

    private ControllerPresenter mControllerPresenter;
    @Mock
    private DevicesRepository mDevicesRepository;

    @Mock
    private ControllerContract.View mControllerView;

    @Captor
    private ArgumentCaptor<DevicesRepository.GetControllerStatusCallback> mGetControllerStatusCallbackCaptor;

    @Captor
    private ArgumentCaptor<DevicesRepository.SendCommandCallback> mSendCommandCallbackCaptor;

    @Captor
    private ArgumentCaptor<DevicesRepository.GetControllerDataCallback> mGetControllerDataCaptor;

    private Controller controller = new Controller(1234, "2.3.7",
            new Date(), "200 OK");

    @Before
    public void setupDevicePresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mControllerPresenter = new ControllerPresenter(mDevicesRepository, mControllerView);
    }


    @Test
    public void getControllerStatus() {
        // When updating controller status...
        mControllerPresenter.getControllerStatus();

        // Controller is up
        verify(mDevicesRepository).getControllerStatus(mGetControllerStatusCallbackCaptor.capture());
        mGetControllerStatusCallbackCaptor.getValue().onControllerUp();

        // Status is updated in view.
        verify(mControllerView).showControllerStatusUp();
    }

    @Test
    public void getControllerStatusError() {
        // When updating controller status...
        mControllerPresenter.getControllerStatus();

        // Controller is down
        verify(mDevicesRepository).getControllerStatus(mGetControllerStatusCallbackCaptor.capture());
        mGetControllerStatusCallbackCaptor.getValue().onControllerDown("Test Error");

        // Status is updated in view.
        verify(mControllerView).showControllerStatusError(eq("Test Error"));
    }

    @Test
    public void restartControllerAndShowStatus() {
        // When restarting controller
        mControllerPresenter.restartController();

        // Progress indicator is shown
        verify(mControllerView).setProgressIndicator(true);
        // Show controller will restart message
        verify(mControllerView).showControllerWillRestartMessage();

        // Controller restart command is sent
        verify(mDevicesRepository).sendCommand(eq(DEVICE_SPECIAL_CONTROLLER), any(String.class) , mSendCommandCallbackCaptor.capture());
        mSendCommandCallbackCaptor.getValue().onCommandSent();

        // Progress indicator is hidden and message is shown in the UI
        verify(mControllerView).setProgressIndicator(false);
        verify(mControllerView).showControllerHasRestarted();
    }

    @Test
    public void restartControllerErrorAndShowStatus() {
        // When restarting controller
        mControllerPresenter.restartController();

        // Progress indicator is shown
        verify(mControllerView).setProgressIndicator(true);

        // Controller restart command is sent, error occurs
        verify(mDevicesRepository).sendCommand(eq(DEVICE_SPECIAL_CONTROLLER), any(String.class) , mSendCommandCallbackCaptor.capture());
        mSendCommandCallbackCaptor.getValue().onCommandFailed("Test Error");

        // Progress indicator is hidden and message is shown in the UI
        verify(mControllerView).setProgressIndicator(false);
        verify(mControllerView).showControllerRestartError(eq("Test Error"));
    }

    @Test
    public void getControllerDataAndLoadIntoView() {
        mControllerPresenter.getControllerData();
        verify(mDevicesRepository).getControllerData(mGetControllerDataCaptor.capture());

        mGetControllerDataCaptor.getValue().onControllerDataLoaded(controller);

        verify(mControllerView).showControllerData(eq(controller));
    }

    @Test
    public void getControllerDataAndShowError() {
        mControllerPresenter.getControllerData();
        verify(mDevicesRepository).getControllerData(mGetControllerDataCaptor.capture());

        mGetControllerDataCaptor.getValue().onControllerDataLoadError("Error");

        verify(mControllerView).showControllerDataError(eq("Error"));
    }
}
