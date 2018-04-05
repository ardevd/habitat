package no.aegisdynamics.habitat.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the temperature converter helper class
 */

public class TemperatureConverterHelperTest {

    @Test
    public void convertKelvinToCelsiusTest() throws Exception {
        assertEquals(TemperatureConverterHelper.convertKelvinToCelsius(313.15), 40.0);
        assertEquals(TemperatureConverterHelper.convertKelvinToCelsius(273.15), 0.0);
    }

    @Test
    public void convertKelvinToFahrenheitTest() throws Exception {
        assertEquals(TemperatureConverterHelper.convertKelvinToFahrenheit(313.15), 104.27);
        assertEquals(TemperatureConverterHelper.convertKelvinToFahrenheit(273.15), 32.27);
    }
}
