package mordeno.jeromejohn.weatherservice.service;

import mordeno.jeromejohn.weatherservice.client.WeatherClient;
import mordeno.jeromejohn.weatherservice.config.CacheConfiguration;
import mordeno.jeromejohn.weatherservice.dto.WeatherDetails;
import mordeno.jeromejohn.weatherservice.exceptions.WeatherProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

/**
 * WeatherService returns unified weather data.
 * Data is cached for 3 seconds. If both providers failed, it will try to get data from the fallback cache,
 * which is updated every time the providers return data.
 * If no cache data is also available, it will throw an WeatherProviderException to the controlleradvice.
 */
@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherClient weatherClient;
    private final CacheManager cacheManager;

    public WeatherService(WeatherClient weatherClient, CacheManager cacheManager) {
        this.weatherClient = weatherClient;
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = CacheConfiguration.WEATHER_CACHE, key = "#location")
    public WeatherDetails getWeather(String location) {
        try {
            log.info("Getting weather data for location {}", location);
            WeatherDetails weatherDetails = weatherClient.getWeather(location);
            updateFallbackCache(location, weatherDetails);
            return weatherDetails;
        } catch (ResourceAccessException | NullPointerException | WeatherProviderException e) {
            log.warn("Calling weather providers failed, trying cached data. Error: {}", e.getMessage());
            return tryFromCache(location);
        }
    }

    private WeatherDetails tryFromCache(String location) {
        Cache fallbackCache = cacheManager.getCache(CacheConfiguration.WEATHER_FALLBACK_CACHE);
        if (fallbackCache != null) {
            WeatherDetails cachedWeather = fallbackCache.get(location, WeatherDetails.class);
            if (cachedWeather != null) return cachedWeather;
        }
        throw new WeatherProviderException("All providers failed and no cached data available for location: " + location);
    }

    private void updateFallbackCache(String location, WeatherDetails weatherDetails) {
        Cache fallbackCache = cacheManager.getCache(CacheConfiguration.WEATHER_FALLBACK_CACHE);
        if (fallbackCache != null) {
            fallbackCache.put(location, weatherDetails);
        }
    }
}
