package com.jessevgool.trivia_backend.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 *
 * @author Jesse van Gool
 */
@Configuration
@EnableCaching
public class CacheConfig {
 @Bean
    public Caffeine<Object,Object> caffeine() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(Duration.ofMinutes(60));
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object,Object> caffeine) {
        var manager = new CaffeineCacheManager("categories");
        manager.setCaffeine(caffeine);
        return manager;
    }
}
