package no.aegisdynamics.habitat.util;

public class HostnameHelper {

    private static final String SSLPORT = ":443";

    private HostnameHelper() {
        // Required empty constructor
    }

    public static String updateHostnameWhenSSLIsEnabled(String hostnameString) {
        // Only append port number if hostname is of reasonable length
        if (hostnameString.length() > 3 && !hostnameString.endsWith(SSLPORT)) {
            hostnameString = String.format("%s%s", hostnameString, SSLPORT);
            return hostnameString;
        } else {
            return hostnameString;
        }
    }

    public static String updateHostnameWhenSSLIsDisabled(String hostnameString) {
        // Remove SSL port if its there.
        if (hostnameString.endsWith(SSLPORT)) {
            hostnameString = hostnameString.replace(SSLPORT, "");
        }
        return hostnameString;
    }
}
