package no.aegisdynamics.habitat.util;


import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GooglePlayServicesHelper {

    public static boolean checkForGooglePlayServices(Activity activity, int REQUEST_CODE) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, REQUEST_CODE)
                        .show();
            } else {
            }
            return false;
        }
        return true;
    }
}
