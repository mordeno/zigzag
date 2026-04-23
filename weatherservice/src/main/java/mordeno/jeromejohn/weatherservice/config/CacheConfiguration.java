package mordeno.jeromejohn.weatherservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String WEATHER_CACHE = "weatherCache";
    public static final String WEATHER_FALLBACK_CACHE = "weatherFallbackCache";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // short-lived cache, expires after 3 seconds
        cacheManager.registerCustomCache(WEATHER_CACHE,
                Caffeine.newBuilder()
                        .expireAfterWrite(3, TimeUnit.SECONDS)
                        .maximumSize(500)
                        .build());

        // long-lived cache, fallback when both providers fail, expires after 30 minutes
        // This can be in database or redis
        cacheManager.registerCustomCache(WEATHER_FALLBACK_CACHE,
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .build());

        return cacheManager;
    }
}
