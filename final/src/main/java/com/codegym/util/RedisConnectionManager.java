package com.codegym.util;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Manages Redis connections using the Lettuce client.
 * Implements AutoCloseable to ensure proper resource cleanup.
 */
public class RedisConnectionManager implements AutoCloseable {
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    
    public RedisConnectionManager(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.connection = redisClient.connect();
    }
    
    public RedisCommands<String, String> sync() {
        return connection.sync();
    }
    
    @Override
    public void close() {
        if (connection != null) {
            connection.close();
        }
    }
}
