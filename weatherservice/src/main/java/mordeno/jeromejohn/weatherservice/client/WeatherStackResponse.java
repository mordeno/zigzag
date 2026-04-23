package mordeno.jeromejohn.weatherservice.client;


import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherStackResponse {

    @JsonProperty("current")
    private Current current;

    private static class Current {
        @JsonProperty("temperature")
        private int temperature;

        @JsonProperty("wind_speed")
        private int windSpeed;
    }

    public int getTemperature() {
        return current.temperature;
    }

    public int getWindSpeed() {
        return current.windSpeed;
    }
}
