package com.codegym.util;

/**
 * Utility class for managing Redis cache keys and expiration times.
 */
public class CacheKeyUtil {
    public static final int CACHE_EXPIRATION_SECONDS = 300;
    
    private CacheKeyUtil() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    public static String getPaginatedCitiesKey(int offset, int limit) {
        return String.format("cities:paginated:%d:%d", offset, limit);
    }
}
