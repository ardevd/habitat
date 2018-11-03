package no.aegisdynamics.habitat.setup;


import android.support.annotation.NonNull;


interface SetupContract {

    interface View {

        void showValidSetup();

        void showInvalidSetup(String error);

        void showEmptyCustomName();

        void setProgressIndicator(boolean active);

        void showEmptyHostname();

        void showInvalidHostname(String error);

        void showHostnameValidCredentialsRequired();

        void showHostnameAnonymousAccessApproved();

        void showInvalidCredentials(String error);

        void showValidCredentials();

    }

    interface UserActionsListener {

        void verifyHostname(@NonNull String hostname);

        void verifyCredentials();

        void saveSetupData(@NonNull String customName);
    }
}
