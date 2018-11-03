package no.aegisdynamics.habitat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple helper to manage device favorites
 */

public class FavoritesHelper {

    private FavoritesHelper() {
        // Required private empty constructor
    }

    public static Set<String> getFavoriteDevicesFromPrefs(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        return new HashSet<>(settings.getStringSet("favorite_devices", new HashSet<String>()));
    }

    private static void putFavoriteDevicesInPrefs(Context context, Set<String> favorites) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet("favorite_devices", favorites);
        editor.apply();
    }

    public static void addDeviceIdToFavorites(Context context, String deviceId) {
        Set<String> favorites = getFavoriteDevicesFromPrefs(context);
        favorites.add(deviceId);
        putFavoriteDevicesInPrefs(context, favorites);
    }

    public static void removeDeviceIdFromFavorites(Context context, String deviceId) {
        Set<String> favorites = getFavoriteDevicesFromPrefs(context);
        favorites.remove(deviceId);
        putFavoriteDevicesInPrefs(context, favorites);
    }
}
