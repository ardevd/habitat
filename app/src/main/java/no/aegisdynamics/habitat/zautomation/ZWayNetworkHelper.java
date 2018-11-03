package no.aegisdynamics.habitat.zautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.google.common.base.Charsets;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import no.aegisdynamics.habitat.provider.DeviceDataContract;
import no.aegisdynamics.habitat.util.DeviceStatusHelper;
import no.aegisdynamics.habitat.util.KeyStoreHelper;

/**
 * Helper class that provides hostnames, credentials, etc.
 */

public class ZWayNetworkHelper implements DeviceDataContract {

    private ZWayNetworkHelper() {
        // Required private constructor
    }

    public static Map<String, String> getAuthenticationHeaders(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        HashMap<String, String> params = new HashMap<>();
        if (!settings.getBoolean("zway_anonymous", true)) {
            // Retrieve encrypted credentials
            String encryptedUsername = settings.getString("zway_username", "");
            String encryptedPassword = settings.getString("zway_password", "");
            String encryptionIvUsername = settings.getString("zway_username_iv", "");
            String encryptionIvPassword = settings.getString("zway_password_iv", "");
            byte[] ivUsername = encryptionIvUsername.getBytes(Charsets.ISO_8859_1);
            byte[] ivPassword = encryptionIvPassword.getBytes(Charsets.ISO_8859_1);
            // Decrypt credentials
            KeyStoreHelper ksh = new KeyStoreHelper();
            try {
                String username = ksh.decryptStringWithKeyStore(encryptedUsername.getBytes(Charsets.ISO_8859_1),
                        ivUsername);
                String password = ksh.decryptStringWithKeyStore(encryptedPassword.getBytes(Charsets.ISO_8859_1),
                        ivPassword);
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
            } catch (Exception ex) {
                //TODO: handle exceptions here.
                ex.printStackTrace();
            }
        }
        return params;
    }

    private static String getZWayServerHostname(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("zway_hostname", "10.10.10.100:8083");
    }

    public static String getZwayDevicesUrl(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/devices", getURLPrefix(context), getZWayServerHostname(context));
    }

    public static String getZwayLocationsUrl(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/locations", getURLPrefix(context), getZWayServerHostname(context));
    }

    public static String getZwayLocationUrl(Context context, int locationId) {
        return String.format(Locale.getDefault(),
                "%s%s/ZAutomation/api/v1/locations/%d", getURLPrefix(context),
                getZWayServerHostname(context), locationId);
    }

    public static String getZwayDeviceImageUrl(Context context, String imageName, String deviceType, String deviceState) {
        String baseImageString = String.format("%s%s/smarthome/storage/img/icons/", getURLPrefix(context),
                getZWayServerHostname(context));

        // Some device types use a static icon not based on device status.
        switch(deviceType) {
            case DEVICE_TYPE_SWITCH_CONTROL:
                return String.format("%s%s", baseImageString, "switch-control");
        }

        // Update the base image string now that we are dealing with state aware device icons.
        baseImageString = String.format("%s%s/smarthome/storage/img/icons/%s", getURLPrefix(context),
                getZWayServerHostname(context), imageName);

        try {
            return String.format("%s%s", baseImageString,
                    DeviceStatusHelper.parseStatusToIconSuffix(deviceType, deviceState));
        } catch (IllegalArgumentException ex) {
            return baseImageString;
        }
    }

    public static String getZwayDevicePlaceholderImageUrl(Context context) {
        return String.format("%s%s/smarthome/storage/img/icons/placeholder", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    public static String getZwayLocationImageUrl(Context context, String imageName) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.getBoolean("use_user_images", true)) {
            return String.format("%s%s/ZAutomation/api/v1/load/image/%s", getURLPrefix(context),
                    getZWayServerHostname(context), imageName);
        } else {
            return String.format("%s%s/smarthome/storage/img/rooms/%s", getURLPrefix(context),
                    getZWayServerHostname(context), imageName);
        }
    }

    public static String getZwayDeviceUrl(Context context, String deviceId) {
        return String.format("%s%s/ZAutomation/api/v1/devices/%s", getURLPrefix(context),
                getZWayServerHostname(context), deviceId);
    }

    public static String getZwayControllerRestartUrl(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/restart", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    public static String getZwayControllerStatusUrl(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/status", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    /**
     * @param context - valid context
     * @return - API url for notifications from the last 24 hours.
     */
    public static String getZwayNotificationsUrl(Context context) {
        long currentEpoch = System.currentTimeMillis();
        return String.format(Locale.getDefault(),
                "%s%s/ZAutomation/api/v1/notifications?since=%d", getURLPrefix(context),
                getZWayServerHostname(context), (currentEpoch - 43200000));
    }

    public static String getZwayNotificationUpdateUrl(Context context, long notificationId) {
        return String.format(Locale.getDefault(),
                "%s%s/ZAutomation/api/v1/notifications/%d", getURLPrefix(context),
                getZWayServerHostname(context), notificationId);
    }

    public static String getZwayNotificationDeleteUrl(Context context, long notificationId) {
        return String.format(Locale.getDefault(),
                "%s%s/ZAutomation/api/v1/notifications/%d", getURLPrefix(context),
                getZWayServerHostname(context), notificationId);
    }

    public static String getZwayHabitatAppRegisterTokenUrl(Context context, String deviceToken, String deviceIdentifier) {
        return String.format("%s%s/ZAutomation/api/v1/devices/HabitatAppSupport/command/registerApp?hubId=1&os=android&title=%s&token=%s",
                getURLPrefix(context),
                getZWayServerHostname(context), deviceIdentifier, deviceToken);
    }

    public static String getZwayBackupURL(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/backup", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    public static String getZwayAppSupportModule(Context context) {
        return String.format("%s%s/smarthome/#/apps/online/423", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    public static String getZwayProfileUrl(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/session", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    public static String getZwayControllerDataUrl(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/system/info", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    public static String getZwayModulesUrl(Context context) {
        return String.format("%s%s/ZAutomation/api/v1/modules", getURLPrefix(context),
                getZWayServerHostname(context));
    }

    private static String getURLPrefix(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useSSL = settings.getBoolean("zway_ssl", false);
        if (useSSL) {
            return "https://";
        } else {
            return "http://";
        }
    }

    public static String getZwayModuleIconUrl(Context context, String moduleId, String icon) {
        return String.format("%s%s/ZAutomation/api/v1/load/modulemedia/%s/%s", getURLPrefix(context),
                getZWayServerHostname(context), moduleId, icon);
    }

    public static String getZwayModuleDeleteUrl(Context context, String moduleId) {
        return String.format("%s%s/ZAutomation/api/v1/delete/%s", getURLPrefix(context),
                getZWayServerHostname(context), moduleId);
    }

    public static String getZwayModuleInstallurl(Context context, String url) {
        return String.format("%s%s/ZAutomation/api/v1/modules/install?moduleUrl=%s", getURLPrefix(context),
                getZWayServerHostname(context), url);
    }
}