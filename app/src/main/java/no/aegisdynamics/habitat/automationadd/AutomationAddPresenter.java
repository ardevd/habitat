package no.aegisdynamics.habitat.automationadd;

import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.data.automation.AutomationsRepository;
import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.data.device.DevicesRepository;
import no.aegisdynamics.habitat.util.DateHelper;


public class AutomationAddPresenter implements AutomationAddContract.UserActionsListener {

    private final AutomationsRepository mAutomationsRepository;
    private final AutomationAddContract.View mAutomationAddView;
    private final DevicesRepository mDevicesRepository;

    public AutomationAddPresenter(@NonNull DevicesRepository devicesRepository,
                                  @NonNull AutomationsRepository automationsRepository,
                                  @NonNull AutomationAddContract.View automationAddView) {
        mAutomationsRepository = automationsRepository;
        mAutomationAddView = automationAddView;
        mDevicesRepository = devicesRepository;
    }

    @Override
    public void saveAutomation(@NonNull Automation automation) {
        if (automation.isEmpty()) {
            mAutomationAddView.showEmptyAutomationError();
        } else if (!DateHelper.isValidAutomationTimestamp(automation.getTrigger())) {
            mAutomationAddView.showInvalidAutomationTrigger();
        } else {
            mAutomationsRepository.createAutomation(automation, new AutomationsRepository.CreateAutomationCallback() {

                @Override
                public void onAutomationCreated() {
                    mAutomationAddView.showAutomationAdded();
                }

                @Override
                public void onAutomationCreateError(String error) {
                    mAutomationAddView.showAutomationAddError(error);
                }
            });
        }
    }

    @Override
    public void loadDevices() {
        // Get list of devices from API server.
        mDevicesRepository.getDevices(new DevicesRepository.LoadDevicesCallback() {

            @Override
            public void onDevicesLoaded(List<Device> devices) {
                mAutomationAddView.showDevices(devices);
            }

            @Override
            public void onDevicesLoadError(String error) {
                mAutomationAddView.showDevicesLoadError(error);
            }
        });
    }

    @Override
    public void generateDefaultTime() {
        mAutomationAddView.showDefaultTime(DateHelper.getDefaultTimestampString());
    }
}
