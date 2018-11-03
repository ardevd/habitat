package no.aegisdynamics.habitat.util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;

import no.aegisdynamics.habitat.R;

public class ErrorParserHelper {

    private static final String TAG = "Network";

    private ErrorParserHelper() {
        // Required empty private constructor
    }

    public static String parseErrorToErrorMessage(Context context, Throwable errorObj) {
        if (context != null) {
            if (errorObj instanceof TimeoutError) {
                return context.getString(R.string.error_timeout);
            } else if (errorObj instanceof NoConnectionError) {
                LogHelper.logError(context, TAG, errorObj.getMessage());
                return context.getString(R.string.error_no_connection);
            } else if (errorObj instanceof AuthFailureError) {
                return context.getString(R.string.devices_authentication_error);
            } else {
                return errorObj.getMessage();
            }
        } else {
            return errorObj.getMessage();
        }
    }
}
