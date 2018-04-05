package no.aegisdynamics.habitat.util;


import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

public class PaletteHelper {

    // Generate palette synchronously and return it
    public static Palette createPaletteSync(Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }

    // Return a palette's vibrant swatch after checking that it exists
    public static Palette.Swatch checkVibrantSwatch(Palette p) {
        Palette.Swatch vibrant = p.getDarkVibrantSwatch();
        if (vibrant != null) {
            return vibrant;
        }
        // Throw error
        return null;
    }

    public static Palette.Swatch checkLightMutedSwatch(Palette p) {
        Palette.Swatch lightMuted = p.getLightMutedSwatch();
        if (lightMuted != null) {
            return lightMuted;
        }
        // Throw error
        return null;
    }
}
