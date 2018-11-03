package no.aegisdynamics.habitat.automationadd;


import android.support.annotation.NonNull;

import java.util.List;

import no.aegisdynamics.habitat.data.automation.Automation;
import no.aegisdynamics.habitat.data.device.Device;

interface AutomationAddContract {

    interface View {

        void showEmptyAutomationError();

        void showInvalidAutomationTrigger();

        void showAutomationAdded();

        void showAutomationAddError(String error);

        void showDefaultTime(String timestring);

        void showDevices(List<Device> devices);

        void showDevicesLoadError(String error);

    }

    interface UserActionsListener {

        void saveAutomation(@NonNull Automation automation);
        void loadDevices();
        void generateDefaultTime();
    }
}
