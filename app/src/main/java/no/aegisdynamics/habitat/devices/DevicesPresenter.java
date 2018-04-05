package no.aegisdynamics.habitat.devices;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Listens to user actions from the UI ({@link DevicesFragment}), retrieves the data and updates the
 * UI as required.
 */
public class DevicesPresenter implements DevicesContract.UserActionsListener, DeviceDataContract {

    private final DevicesRepository mDevicesRepository;
    private final DevicesContract.View mDevicesView;

    public DevicesPresenter(@NonNull DevicesRepository devicesRepository, @NonNull DevicesContract.View devicesView){
        mDevicesRepository = devicesRepository;
        mDevicesView = devicesView;
    }

    @Override
    public void loadDevices() {
        // Get list of devices from API server.
        mDevicesView.setProgressIndicator(true);
        mDevicesRepository.getDevices(new DevicesRepository.LoadDevicesCallback() {

            @Override
            public void onDevicesLoaded(List<Device> devices) {
                mDevicesView.setProgressIndicator(false);
                mDevicesView.showDevices(devices);
            }

            @Override
            public void onDevicesLoadError(String error) {
                mDevicesView.setProgressIndicator(false);
                mDevicesView.showDevicesLoadError(error);
            }
        });
    }

    @Override
    public void openDeviceDetails(@NonNull Device requestedDevice) {
        mDevicesView.showDeviceDetailUI(requestedDevice);
    }

    @Override
    public void sendCommand(@NonNull Device requestedDevice, @NonNull String command) {
        // Send device command to server.
        mDevicesView.setProgressIndicator(true);
        mDevicesRepository.sendCommand(requestedDevice.getId(), command, new DevicesRepository.SendCommandCallback() {


            @Override
            public void onCommandFailed(String error) {
                mDevicesView.showCommandFailedMessage(error);
                mDevicesView.setProgressIndicator(false);
            }

            @Override
            public void onCommandSent() {
                mDevicesView.setProgressIndicator(false);
                mDevicesView.showCommandStatusMessage(true);
            }
        });
    }
}