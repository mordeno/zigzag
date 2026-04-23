package mordeno.jeromejohn.weatherservice.client;

import mordeno.jeromejohn.weatherservice.dto.WeatherDetails;

public interface WeatherClient {

    WeatherDetails getWeather(String location);
}
