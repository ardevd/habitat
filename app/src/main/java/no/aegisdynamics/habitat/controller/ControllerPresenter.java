package no.aegisdynamics.habitat.controller;

import android.support.annotation.NonNull;

import no.aegisdynamics.habitat.data.device.Controller;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.provider.DeviceDataContract;

/**
 * Listens to user actions from the UI ({@link ControllerFragment}), retrieves the data and updates the
 * UI as required.
 */

public class ControllerPresenter implements ControllerContract.UserActionsListener, DeviceDataContract {

    private final DevicesRepository mDevicesRepository;
    private final ControllerContract.View mControllerView;

    public ControllerPresenter(@NonNull DevicesRepository devicesRepository,
                               @NonNull ControllerContract.View controllerView) {
        mDevicesRepository = devicesRepository;
        mControllerView = controllerView;
    }

    @Override
    public void getControllerStatus() {
        // Get Z-Way controller status
        mDevicesRepository.getControllerStatus(new DevicesRepository.GetControllerStatusCallback() {

            @Override
            public void onControllerUp() {
                mControllerView.showControllerStatusUp();
            }

            @Override
            public void onControllerDown(String error) {
                mControllerView.showControllerStatusError(error);
            }
        });

    }

    @Override
    public void getControllerData() {
        // Get Z-Way controller data
        mDevicesRepository.getControllerData(new DevicesRepository.GetControllerDataCallback() {
            @Override
            public void onControllerDataLoaded(Controller controller) {
                mControllerView.showControllerData(controller);
            }

            @Override
            public void onControllerDataLoadError(String error) {
                mControllerView.showControllerDataError(error);
            }
        });

    }

    @Override
    public void restartController() {
        mControllerView.setProgressIndicator(true);
        mControllerView.showControllerWillRestartMessage();
        mDevicesRepository.sendCommand(DEVICE_SPECIAL_CONTROLLER, "", new DevicesRepository.SendCommandCallback() {


            @Override
            public void onCommandFailed(String error) {
                mControllerView.showControllerRestartError(error);
                mControllerView.setProgressIndicator(false);
            }

            @Override
            public void onCommandSent() {
                mControllerView.setProgressIndicator(false);
                mControllerView.showControllerHasRestarted();
            }
        });

    }
}
