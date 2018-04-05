package no.aegisdynamics.habitat.data.profile;


import android.support.annotation.NonNull;

public interface ProfileRepository {

    interface GetProfileCallback {
        void onProfileLoaded(Profile profile);
        void onProfileLoadError(String error);
    }

    void getProfile(@NonNull String username, @NonNull GetProfileCallback callback);
}
