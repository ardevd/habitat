package no.aegisdynamics.habitat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Charsets;

import no.aegisdynamics.habitat.R;

/**
 * Helper class that retrieves and decrypts user credentials.
 */

public class UserCredentialsManager {

    public static String getZwayUsername(Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.getBoolean("zway_anonymous", true)){
            return context.getString(R.string.base_anonymous);
        }
        KeyStoreHelper ksh = new KeyStoreHelper();
        try {
            return ksh.decryptStringWithKeyStore(settings.getString("zway_username", "").getBytes(Charsets.ISO_8859_1),
                    settings.getString("zway_username_iv", "").getBytes(Charsets.ISO_8859_1));
        } catch (Exception ex) {
            return "";
        }
    }
}
