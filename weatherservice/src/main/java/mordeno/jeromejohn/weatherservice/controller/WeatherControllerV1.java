package mordeno.jeromejohn.weatherservice.controller;

import mordeno.jeromejohn.weatherservice.dto.WeatherDetails;
import mordeno.jeromejohn.weatherservice.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Weather API that gets weather data by location.
 */
@RestController
@RequestMapping("/v1")
public class WeatherControllerV1 {

    private static final Logger log = LoggerFactory.getLogger(WeatherControllerV1.class);

    private final WeatherService weatherService;

    public WeatherControllerV1(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public ResponseEntity<WeatherDetails> getWeather(String city) {
        log.info("Received request to get weather for city: {}", city);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(weatherService.getWeather(city));
    }
}
