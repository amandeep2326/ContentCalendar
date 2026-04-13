package com.example.content_calendar.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                // content by id — 10 min TTL
                .withCacheConfiguration("content",
                        defaultConfig.entryTtl(Duration.ofMinutes(10)))
                // content by author — 10 min TTL
                .withCacheConfiguration("contentByAuthor",
                        defaultConfig.entryTtl(Duration.ofMinutes(10)))
                // author profile — 30 min TTL (changes less frequently)
                .withCacheConfiguration("author",
                        defaultConfig.entryTtl(Duration.ofMinutes(30)))
                // all tags list — 1 hour TTL (tags rarely change)
                .withCacheConfiguration("tags",
                        defaultConfig.entryTtl(Duration.ofHours(1)))
                // tag by id — 1 hour TTL
                .withCacheConfiguration("tag",
                        defaultConfig.entryTtl(Duration.ofHours(1)))
                .build();
    }
}
