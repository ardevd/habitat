package no.aegisdynamics.habitat.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Snackbar helper class for showing snackbar messages
 */

public class SnackbarHelper {

    public static void showSimpleSnackbarMessage(String message, View view) {
        try {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } catch (NullPointerException ex) {
            // View is not available. Fragment probably detached. No action required.
            ex.printStackTrace();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
