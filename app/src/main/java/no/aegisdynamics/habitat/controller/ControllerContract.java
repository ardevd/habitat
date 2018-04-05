package no.aegisdynamics.habitat.controller;

/**
 * Specifies the contract between the Controller view and the presenter.
 */

public interface ControllerContract {

    interface View {
        void showControllerWillRestartMessage();
        void showControllerHasRestarted();
        void showControllerRestartError(String error);
        void showControllerStatusUp();
        void showControllerStatusError(String error);
        void showRestartControllerDialog();
        void setProgressIndicator(boolean active);
    }

    interface UserActionsListener {
        void getControllerStatus();
        void restartController();
    }
}
