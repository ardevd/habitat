package no.aegisdynamics.habitat.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import no.aegisdynamics.habitat.data.device.Device;
import no.aegisdynamics.habitat.devicedetail.DeviceDetailActivity;

/**
 * Helper class for common app navigation operations
 */
public class HabitatNavigator {

    private HabitatNavigator() {
        // Required empty constructor
    }

    public static void showDeviceDetailUI(@NonNull final Device device, @Nullable final Context context) {
        if (context != null) {
            // in it's own Activity, since it makes more sense that
            Intent intent = new Intent(context, DeviceDetailActivity.class);
            intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE, device);
            context.startActivity(intent);
        }
    }
}
