package no.aegisdynamics.habitat.devices;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class DevicesPresenterTest implements DeviceDataContract {

    // Dummy Device for testing
    private static Device DEVICE = new Device("test-device-01", "Test Device", "switchBinary", "Office", 1, 1, null, "on", 0, 0, null, null, "icon");

    // List with dummy devices for testing
    private static List<Device> DEVICES = Lists.newArrayList(new Device("test-device-01", "Test Device", "switchBinary", "Office", 1, 1, null, "on", 0, 0, null, null, null),
            new Device("test-device-02", "Test Device 02", "doorlock", "Living Room", 2, 2, null, "close",0 , 0, null, null, "icon"));

    @Mock
    private DevicesRepository mDevicesRepository;

    @Mock
    private DevicesContract.View mDevicesView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<DevicesRepository.LoadDevicesCallback> mLoadDevicesCallbackCaptor;

    @Captor
    private ArgumentCaptor<DevicesRepository.SendCommandCallback> mSendCommandCallbackCaptor;

    private DevicesPresenter mDevicesPresenter;

    @Before
    public void setupDevicePresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mDevicesPresenter = new DevicesPresenter(mDevicesRepository, mDevicesView);
    }

    @Test
    public void loadDevicesFromRepositoryAndLoadIntoView() {
        // Given an initialized DevicesPresenter with initialized devices
        // When loading of devices is requested
        mDevicesPresenter.loadDevices();

        // Callback is captured and invoked with stubbed devices
        verify(mDevicesView).setProgressIndicator(true);
        verify(mDevicesRepository).getDevices(mLoadDevicesCallbackCaptor.capture());
        mLoadDevicesCallbackCaptor.getValue().onDevicesLoaded(DEVICES);

        // Progress indicator is hidden and devices are shown in the UI
        verify(mDevicesView).setProgressIndicator(false);
        verify(mDevicesView).showDevices(DEVICES);
    }

    @Test
    public void loadDevicesFromRepositoryFailureAndShowError() {
        // Given an initialized DevicesPresenter with initialized devices
        // When loading of devices is requested
        mDevicesPresenter.loadDevices();

        // Callback is captured and invoked with stubbed devices
        verify(mDevicesView).setProgressIndicator(true);
        verify(mDevicesRepository).getDevices(mLoadDevicesCallbackCaptor.capture());
        mLoadDevicesCallbackCaptor.getValue().onDevicesLoadError("Test error");

        // Progress indicator is hidden and error message is shown in the UI
        verify(mDevicesView).setProgressIndicator(false);
        verify(mDevicesView).showDevicesLoadError(eq("Test error"));
    }


    @Test
    public void sendCommandToDeviceFromDevices() {
        // When sending a command to a device
        mDevicesPresenter.sendCommand(DEVICE, "on");

        // Progress indicator is shown
        verify(mDevicesView).setProgressIndicator(true);

        // Command is sent
        verify(mDevicesRepository).sendCommand(eq(DEVICE.getId()), eq("on"), mSendCommandCallbackCaptor.capture());
        mSendCommandCallbackCaptor.getValue().onCommandSent();

        // Progress indicator is hidden and message is shown in the UI
        verify(mDevicesView).setProgressIndicator(false);
        verify(mDevicesView).showCommandStatusMessage(eq(true));
    }

    @Test
    public void sendCommandFailedToDeviceFromDevice() {
        // When sending a command to a device
        mDevicesPresenter.sendCommand(DEVICE, "on");

        // Progress indicator is shown
        verify(mDevicesView).setProgressIndicator(true);

        // Command is sent
        verify(mDevicesRepository).sendCommand(eq(DEVICE.getId()), eq("on"), mSendCommandCallbackCaptor.capture());
        mSendCommandCallbackCaptor.getValue().onCommandFailed("Error");

        // Progress indicator is hidden and message is shown in the UI
        verify(mDevicesView).setProgressIndicator(false);
        verify(mDevicesView).showCommandFailedMessage(eq("Error"));
    }

    @Test
    public void clickOnDevice_ShowsDetailUi() {

        // When open device details is requested
        mDevicesPresenter.openDeviceDetails(DEVICE);

        // Then device detail UI is shown
        verify(mDevicesView).showDeviceDetailUI(any(Device.class));
    }
}