package no.aegisdynamics.habitat.util;

/**
 * Helper class for parsing condition code to unicode icon font values.
 */

public class WeatherIconFontHelper {

    public static String parseConditionIconToFontString(String code) {
        switch (code){
            case "50d":
                return "\uf003";
            case "50n":
                return "\uf04a";
            case "01d":
                return "\uf00d";
            case "01n":
                return "\uf02e";
            case "10d":
                return "\uf00b";
            case "10n":
                return "\uf02b";
            case "13d":
                return "\uf0b2";
            case "13n":
                return "\uf0b4";
            case "11d":
                return "\uf010";
            case "11n":
                return "\uf02d";
            case "09d":
                return "\uf00b";
            case "09n":
                return "\uf02b";
            case "04d":
                return "\uf000";
            case "04n":
                return "\uf022";
            default:
                return "";
        }
    }
}
