package no.aegisdynamics.habitat.data.profile;


import android.content.Context;
import android.support.annotation.NonNull;

public class ProfileRepositories {

    private ProfileRepositories() {
        // Required empty instance
    }

    public static synchronized ProfileRepository getRepository(@NonNull Context context,
                                                               @NonNull ProfileServiceApi profileServiceApi) {
        return new ZWayAPIProfileRepository(profileServiceApi, context);
    }
}
