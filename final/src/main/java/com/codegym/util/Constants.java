package com.codegym.util;

/**
 * Application-wide constants.
 */
public final class Constants {
    // Database configuration
    public static final int MYSQL_PORT = 3306;
    public static final String DEFAULT_MYSQL_HOST = "localhost";
    public static final String DEFAULT_MYSQL_DATABASE = "world";
    public static final String DEFAULT_MYSQL_USER = "hibernate_user";
    public static final String DEFAULT_MYSQL_PASSWORD = "hibernate_password";
    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String MYSQL_DIALECT = "org.hibernate.dialect.MySQL8Dialect";
    
    // Redis configuration
    public static final int REDIS_PORT = 6379;
    public static final String DEFAULT_REDIS_HOST = "localhost";
    public static final int REDIS_DATABASE = 0;
    public static final int REDIS_CACHE_TTL = 300; // 5 minutes in seconds
    
    // Pagination and display
    public static final int DEFAULT_CATEGORY_LIMIT = 1000;
    public static final int EXAMPLE_DISPLAY_LIMIT = 5;
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    // Batch processing
    public static final int DEFAULT_BATCH_SIZE = 500;
    
    // City population thresholds
    public static final int METROPOLIS_THRESHOLD = 1_000_000;
    public static final int LARGE_CITY_THRESHOLD = 500_000;
    public static final int MEDIUM_CITY_THRESHOLD = 100_000;
    
    // Environment variable keys
    public static final String ENV_MYSQL_URL = "MYSQL_URL";
    public static final String ENV_MYSQL_USER = "MYSQL_USER";
    public static final String ENV_MYSQL_PASSWORD = "MYSQL_PASSWORD";
    public static final String ENV_REDIS_URL = "REDIS_URL";
    
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }
}
