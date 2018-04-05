package no.aegisdynamics.habitat.util;

/**
 * Helper class for converting bytes to readable strings
 */

public class FileSizeStringifier {

    public static String convertBytesToScaledString(long bytes) {
        if (bytes >= 102400) {
            return String.format("%d MB", bytes/1000000);
        } else if (bytes >= 1024) {
            return String.format("%d kB", bytes/1000);
        }

        return String.format("%d b", bytes);
    }
}
