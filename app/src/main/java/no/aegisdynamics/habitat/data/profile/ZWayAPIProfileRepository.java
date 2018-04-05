package no.aegisdynamics.habitat.data.profile;

import android.content.Context;
import android.support.annotation.NonNull;


public class ZWayAPIProfileRepository implements ProfileRepository {

    private final Context mContext;
    private final ProfileServiceApi mProfileServiceApi;

    public ZWayAPIProfileRepository(@NonNull ProfileServiceApi profileServiceAPI,
                                    @NonNull Context context) {
        mContext = context;
        mProfileServiceApi = profileServiceAPI;
    }

    @Override
    public void getProfile(String username, final @NonNull GetProfileCallback callback) {
        mProfileServiceApi.getProfile(mContext, username, new ProfileServiceApi.ProfileServiceCallback<Profile>() {

            @Override
            public void onLoaded(Profile profile) {
                callback.onProfileLoaded(profile);
            }

            @Override
            public void onError(String error) {
                callback.onProfileLoadError(error);
            }
        });

    }
}
