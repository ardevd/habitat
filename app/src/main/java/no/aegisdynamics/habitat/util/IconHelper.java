package no.aegisdynamics.habitat.util;

import no.aegisdynamics.habitat.R;

public class IconHelper {

    private IconHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static int getSSLIndicatorIcon(boolean sslEnabled) {
        if (sslEnabled) {
            return R.drawable.ic_lock;
        } else {
            return R.drawable.ic_unlock;
        }
    }

    public static int getSSLIndicatorTint(boolean sslEnabled) {
        if (sslEnabled) {
            return R.color.colorWhite;
        } else {
            return R.color.colorAccent;
        }
    }
}
