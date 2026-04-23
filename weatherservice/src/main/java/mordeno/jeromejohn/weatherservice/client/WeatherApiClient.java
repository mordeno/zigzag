package mordeno.jeromejohn.weatherservice.client;

import mordeno.jeromejohn.weatherservice.dto.WeatherDetails;
import mordeno.jeromejohn.weatherservice.exceptions.WeatherProviderException;
import mordeno.jeromejohn.weatherservice.properties.WeatherProviderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * Concrete implementation of WeatherClient
 * WeatherApiClient calls the primary weather provider (WeatherStack) first.
 * If it fails, it will call the failover client (OpenWeatherMap).
 * If both providers fail, it will propagate the exception to WeatherService,
 * which will then try to get data from the fallback cache.
 */
@Component
public class WeatherApiClient implements WeatherClient {

    private static final Logger log = LoggerFactory.getLogger(WeatherApiClient.class);

    private final RestClient primaryClient;
    private final RestClient failoverClient;
    private final WeatherProviderProperties properties;

    public WeatherApiClient(@Qualifier("primaryWeatherClient") RestClient primaryClient,
                            @Qualifier("failoverWeatherClient") RestClient failoverClient,
                            WeatherProviderProperties properties) {
        this.primaryClient = primaryClient;
        this.failoverClient = failoverClient;
        this.properties = properties;
    }

    @Override
    public WeatherDetails getWeather(String location) {
        log.info("Fetching {} weather using WeatherStack", location);

        // hard-coded location: Melbourne
        try {
            WeatherStackResponse response = primaryClient.get()
                    .uri("/current?access_key={accessKey}&query={location}",
                            properties.primary().accessKey(),
                            "Melbourne")
                    .retrieve()
                    .body(WeatherStackResponse.class);
            log.info("Got response from WeatherStack: {}", response);
            return toWeatherDetails(response);
        } catch (ResourceAccessException | NullPointerException | WeatherProviderException e) {
            log.warn("Primary provider failed, trying failover provider. Error: {}", e.getMessage());
            return failoverClient(location); // propagates any exception to WeatherService
        }
    }

    private WeatherDetails failoverClient(String location) {
        log.info("Fetching {} weather using OpenWeatherMap", location);

        // hard-coded location: Melbourne,AU
        OpenWeatherMapResponse response = failoverClient.get()
                .uri("/weather?q={location}&appid={accessKey}",
                        "Melbourne,AU",
                        properties.failover().accessKey())
                .retrieve()
                .body(OpenWeatherMapResponse.class);
        log.info("Got response from OpenWeatherMapResponse: {}", response);
        return toWeatherDetails(response);
    }

    private WeatherDetails toWeatherDetails(WeatherStackResponse response) {
        return new WeatherDetails(
                response.getWindSpeed(),
                response.getTemperature());
    }

    private WeatherDetails toWeatherDetails(OpenWeatherMapResponse response) {
        return new WeatherDetails(
                OpenWeatherMapResponse.toKmph(response.getWindSpeed()),
                OpenWeatherMapResponse.toCelsius(response.getTemperature()));
    }
}
