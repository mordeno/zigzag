package mordeno.jeromejohn.weatherservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "weather.providers")
public record WeatherProviderProperties(
        Provider primary,
        Provider failover) {

    public record Provider(
            String accessKey,
            String baseUrl,
            Timeout timeout) {

        public record Timeout(
                Duration connect,
                Duration read) {
        }
    }
}
