package no.aegisdynamics.habitat.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Helper class for converting temperature units
 */

public class TemperatureConverterHelper {

    private TemperatureConverterHelper() {
        // Required empty private constructor
    }

    public static double convertKelvinToCelsius(double kelvinTemp) {
        return kelvinTemp - 273.15;
    }

    public static double convertKelvinToFahrenheit(double kelvinTemp) {
        double convertedValue = (((kelvinTemp - 273) * 9/5) + 32);

        BigDecimal bd = new BigDecimal(Double.toString(convertedValue));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
