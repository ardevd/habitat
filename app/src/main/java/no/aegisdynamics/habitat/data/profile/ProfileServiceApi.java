package no.aegisdynamics.habitat.data.profile;

import android.content.Context;

/**
 * Defines an interface to the profile service API. All profile requests should be piped
 * through this interface.
 */

public interface ProfileServiceApi {

    interface ProfileServiceCallback<T> {
        void onLoaded(T profile);
        void onError(String error);
    }

    void getProfile(Context context, String username,
                    ProfileServiceApi.ProfileServiceCallback<Profile> callback);
}
