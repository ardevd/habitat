package no.aegisdynamics.habitat.util;


import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GooglePlayServicesHelper {

    private GooglePlayServicesHelper() {
        // Required empty private constructor
    }

    public static boolean checkForGooglePlayServices(Activity activity, int REQUEST_CODE) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, REQUEST_CODE)
                        .show();
            }
            return false;
        }
        return true;
    }
}
