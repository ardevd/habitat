package no.aegisdynamics.habitat.data.weather;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for the Weather model class
 */

public class WeatherTest {

    private static final double WEATHER_TEMPERATURE = 237;
    private static final double WEATHER_TEMPERATURE_MAX = 247;
    private static final double WEATHER_TEMPERATURE_MIN = 230;
    private static final String WEATHER_CONDITION = "clear";
    private static final String WEATHER_ICON = "oc1";
    private static final int WEATHER_CONDITION_CODE = 2;

    private static final Weather WEATHER = new Weather(WEATHER_CONDITION_CODE, WEATHER_CONDITION,
            WEATHER_ICON, WEATHER_TEMPERATURE, WEATHER_TEMPERATURE_MAX, WEATHER_TEMPERATURE_MIN);

    @Test
    public void testGetWeatherTemperature() throws Exception {
        assertEquals(WEATHER_TEMPERATURE, WEATHER.getTemperature());
    }

    @Test
    public void testGetWeatherTemperatureMax() throws Exception {
        assertEquals(WEATHER_TEMPERATURE_MAX, WEATHER.getTemperatureMax());
    }

    @Test
    public void testGetWeatherTemperatureMin() throws Exception {
        assertEquals(WEATHER_TEMPERATURE_MIN, WEATHER.getTemperatureMin());
    }

    @Test
    public void testGetWeatherConditionString() throws Exception {
        assertEquals(WEATHER_CONDITION, WEATHER.getCondition());
    }

    @Test
    public void testGetWeatherConditionCode() throws Exception {
        assertEquals(WEATHER_CONDITION_CODE, WEATHER.getConditionCode());
    }

    @Test
    public void testGetIconName() throws Exception {
        assertEquals(WEATHER_ICON, WEATHER.getConditionIcon());
    }
}
