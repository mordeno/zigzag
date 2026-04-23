package mordeno.jeromejohn.weatherservice.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenWeatherMapResponse {

    @JsonProperty("main")
    private Main main;

    @JsonProperty("wind")
    private Wind wind;

    private static class Main {
        @JsonProperty("temp")
        private double temperature;
    }

    private static class Wind {
        @JsonProperty("speed")
        private double speed;
    }

    public double getTemperature() {
        return main.temperature;
    }

    public double getWindSpeed() {
        return wind.speed;
    }

    public static int toCelsius(double temp) {
        return (int) Math.round(temp - 273.15);
    }

    public static int toKmph(double speed) {
        return (int) Math.round(speed * 3.6);
    }
}
