package no.aegisdynamics.habitat.setup;


import android.support.annotation.NonNull;

import no.aegisdynamics.habitat.data.device.DevicesRepository;

public class SetupPresenter implements SetupContract.UserActionsListener {

    @NonNull
    private final SetupContract.View mSetupView;
    @NonNull
    private final DevicesRepository mDevicesRepository;

    
    public SetupPresenter(@NonNull DevicesRepository devicesRepository,
                                @NonNull SetupContract.View setupView) {

        mDevicesRepository = devicesRepository;
        mSetupView = setupView;
    }

    @Override
    public void verifyHostname(@NonNull String hostname) {
        // Verify that the hostname is not empty.
        if (hostname.length() == 0) {
            mSetupView.showEmptyHostname();
        } else {
            mSetupView.setProgressIndicator(true);
            // Get Z-Way controller status
            mDevicesRepository.getControllerStatus(new DevicesRepository.GetControllerStatusCallback() {

                @Override
                public void onControllerUp() {
                    mSetupView.setProgressIndicator(false);
                    mSetupView.showHostnameAnonymousAccessApproved();
                }

                @Override
                public void onControllerDown(String error) {
                    mSetupView.setProgressIndicator(false);
                    mSetupView.showInvalidHostname(error);
                }
            });
        }
    }

    @Override
    public void verifyCredentials() {
        mSetupView.setProgressIndicator(true);
        // Attempt to get controller status with the supplied credentials
        mDevicesRepository.getControllerStatus(new DevicesRepository.GetControllerStatusCallback() {

            @Override
            public void onControllerUp() {
                mSetupView.setProgressIndicator(false);
                mSetupView.showValidCredentials();
            }

            @Override
            public void onControllerDown(String error) {
                mSetupView.setProgressIndicator(false);
                mSetupView.showInvalidCredentials(error);
            }
        });

    }

    @Override
    public void saveSetupData(@NonNull String customName) {
        // Verify connectivity one final time.
        if (customName.length() == 0) {
            mSetupView.showEmptyCustomName();
        } else {
            mSetupView.setProgressIndicator(true);
            mDevicesRepository.getControllerStatus(new DevicesRepository.GetControllerStatusCallback() {

                @Override
                public void onControllerUp() {
                    mSetupView.setProgressIndicator(false);
                    mSetupView.showValidSetup();
                }

                @Override
                public void onControllerDown(String error) {
                    mSetupView.setProgressIndicator(false);
                    mSetupView.showInvalidSetup(error);
                }
            });
        }
    }
}
