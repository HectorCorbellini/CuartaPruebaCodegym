package com.codegym.config;

import com.codegym.util.Constants;
import io.lettuce.core.RedisClient;

/**
 * Configuration class for Redis settings.
 * Handles Redis client creation and connection management.
 */
public class RedisConfig {
    private final String redisUrl;
    
    public RedisConfig() {
        this.redisUrl = buildRedisUrl();
    }
    
    private String buildRedisUrl() {
        String host = getEnvOrDefault("REDIS_HOST", Constants.DEFAULT_REDIS_HOST);
        int port = Constants.REDIS_PORT;
        return String.format("redis://%s:%d/0", host, port);
    }
    
    private String getEnvOrDefault(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }
    
    public RedisClient createClient() {
        return RedisClient.create(redisUrl);
    }
    
    public String getRedisUrl() {
        return redisUrl;
    }
}
