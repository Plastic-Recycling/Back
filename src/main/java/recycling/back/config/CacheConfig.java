package recycling.back.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine
                .newBuilder()
                .initialCapacity(10)
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES)
        );

        cacheManager.setCacheNames(Arrays.asList("verifyToken", "verifiedEmail"));

        cacheManager.registerCustomCache("verifyToken",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .initialCapacity(3)
                        .maximumSize(100)
                        .build()
        );

        cacheManager.registerCustomCache("verifiedEmail",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .initialCapacity(3)
                        .maximumSize(100)
                        .build()
                );

        return cacheManager;
    }
}
