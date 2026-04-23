package mordeno.jeromejohn.weatherservice.config;

import mordeno.jeromejohn.weatherservice.exceptions.WeatherProviderException;
import mordeno.jeromejohn.weatherservice.properties.WeatherProviderProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

/**
 * The specific providers of primary and failover api clients can be changed by updating the properties file.
 * This configuration only handles ANY errors and throws WeatherProviderException
 */
@Configuration
@EnableConfigurationProperties(WeatherProviderProperties.class)
public class RestClientConfiguration {

    private final WeatherProviderProperties properties;

    public RestClientConfiguration(WeatherProviderProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Qualifier("primaryWeatherClient")
    public RestClient primaryWeatherClient() {
        return buildRestClient(properties.primary());
    }

    @Bean
    @Qualifier("failoverWeatherClient")
    public RestClient failOverWeatherClient() {
        return buildRestClient(properties.failover());
    }

    private RestClient buildRestClient(WeatherProviderProperties.Provider provider) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(provider.timeout().connect())
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(provider.timeout().read());

        return RestClient.builder()
                .baseUrl(provider.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("Accept", "application/json")
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        ((request, response) -> {
                            throw new WeatherProviderException("Provider error: " + response.getStatusCode());
                        }))
                .build();
    }
}
