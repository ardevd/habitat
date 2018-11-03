package no.aegisdynamics.habitat.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.andrognito.flashbar.Flashbar;

import no.aegisdynamics.habitat.R;

/**
 * Snackbar helper class for showing snackbar messages
 */

public class SnackbarHelper {

    private SnackbarHelper() {
        // Required empty private constructor
    }

    public static void showSimpleSnackbarMessage(String message, View view) {
        try {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } catch (NullPointerException | IllegalArgumentException | IllegalStateException ex) {
            // View is not available. Fragment probably detached. No action required.
            ex.printStackTrace();
        }
    }

    public static void showFlashbarErrorMessage(String title, String message, Activity activity) {
        try {
            if (activity != null && !title.isEmpty() && !message.isEmpty()) {

                Flashbar flashbar = new Flashbar.Builder(activity)
                        .gravity(Flashbar.Gravity.BOTTOM)
                        .title(title)
                        .message(message)
                        .backgroundColorRes(R.color.colorPrimaryDark)
                        .showOverlay()
                        .duration(3500)
                        .build();

                flashbar.show();

            }
        } catch (NullPointerException | IllegalArgumentException | IllegalStateException ex) {
            // View not available or message and/or title is null.
            ex.printStackTrace();
        }
    }

    public static void showUnsupportedControllerMessage(String version, Activity activity) {
        if (activity != null) {
            Flashbar flashbar = new Flashbar.Builder(activity)
                    .gravity(Flashbar.Gravity.BOTTOM)
                    .title(activity.getString(R.string.error_unsupported_controller_title))
                    .message(activity.getString(R.string.error_unsupported_controller_desc, version))
                    .backgroundColorRes(R.color.colorPrimaryDark)
                    .showOverlay()
                    .duration(3500)
                    .build();

            flashbar.show();
        }
    }
}
