package no.aegisdynamics.habitat.controller;

import no.aegisdynamics.habitat.data.device.Controller;

/**
 * Specifies the contract between the Controller view and the presenter.
 */

interface ControllerContract {

    interface View {
        void showControllerWillRestartMessage();
        void showControllerHasRestarted();
        void showControllerRestartError(String error);
        void showControllerStatusUp();
        void showControllerStatusError(String error);
        void showRestartControllerDialog();
        void showControllerData(Controller controller);
        void showControllerDataError(String error);
        void setProgressIndicator(boolean active);
        void showControllerUnsupportedVersionMessage(String controllerVersion);
    }

    interface UserActionsListener {
        void getControllerStatus();
        void getControllerData();
        void restartController();
    }
}
