package mordeno.jeromejohn.weatherservice.service;

import mordeno.jeromejohn.weatherservice.client.WeatherClient;
import mordeno.jeromejohn.weatherservice.dto.WeatherDetails;
import mordeno.jeromejohn.weatherservice.exceptions.WeatherProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private WeatherService weatherService;

    private WeatherDetails weatherDetails;

    private WeatherDetails cachedWeatherDetails;

    @BeforeEach
    void setUp() {
        // mock CacheManager to return the mocked Cache
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        weatherDetails = new WeatherDetails(10, 30);
        cachedWeatherDetails = new WeatherDetails(5, 25);

        weatherService = new WeatherService(weatherClient, cacheManager);
    }

    @Test
    void GetWeather_ProvidersRequestValid_ReturnsWeatherDetails() {
        when(weatherClient.getWeather(anyString())).thenReturn(weatherDetails);

        WeatherDetails result = weatherService.getWeather("city");
        assertThat(result).isNotNull();
        assertThat(result.windSpeed()).isEqualTo(10);
        assertThat(result.temperatureDegrees()).isEqualTo(30);
        verify(weatherClient, times(1)).getWeather(anyString());
        verify(cacheManager, times(1)).getCache(anyString());
    }

    @Test
    void GetWeather_ProvidersRequestFailed_ReturnsCachedWeatherDetails() {
        when(weatherClient.getWeather((anyString()))).thenThrow(new WeatherProviderException("Provider error"));
        when(cache.get(anyString(), eq(WeatherDetails.class))).thenReturn(cachedWeatherDetails);

        WeatherDetails result = weatherService.getWeather("city");
        assertThat(result).isNotNull();
        assertThat(result.windSpeed()).isEqualTo(5);
        assertThat(result.temperatureDegrees()).isEqualTo(25);
        verify(weatherClient, times(1)).getWeather(anyString());
        verify(cacheManager, times(1)).getCache(anyString());
        verify(cache, times(1)).get(anyString(), eq(WeatherDetails.class));
    }

    @Test
    void GetWeather_ProvidersRequestFailedAndNoCache_ThrowsException() {
        when(weatherClient.getWeather((anyString()))).thenThrow(new WeatherProviderException("Provider error"));
        when(cache.get(anyString(), eq(WeatherDetails.class))).thenReturn(null);

        assertThatThrownBy(() -> weatherService.getWeather("city"))
                .isInstanceOf(WeatherProviderException.class)
                .hasMessageContaining("All providers failed and no cached data available for location: city");
    }
}
