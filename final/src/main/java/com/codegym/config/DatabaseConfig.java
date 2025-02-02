package com.codegym.config;

import com.codegym.util.Constants;
import org.hibernate.cfg.Environment;
import java.util.Properties;

/**
 * Configuration class for database settings.
 * Handles loading and managing database connection properties.
 */
public class DatabaseConfig {
    private final Properties properties;
    
    public DatabaseConfig() {
        properties = new Properties();
        loadProperties();
    }
    
    private void loadProperties() {
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, buildJdbcUrl());
        properties.put(Environment.USER, getEnvOrDefault("MYSQL_USER", "hibernate_user"));
        properties.put(Environment.PASS, getEnvOrDefault("MYSQL_PASSWORD", "hibernate_password"));
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.SHOW_SQL, "false");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.STATEMENT_BATCH_SIZE, String.valueOf(Constants.DEFAULT_BATCH_SIZE));
    }
    
    private String buildJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/world",
            getEnvOrDefault("MYSQL_HOST", Constants.DEFAULT_MYSQL_HOST),
            Constants.MYSQL_PORT);
    }
    
    private String getEnvOrDefault(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }
    
    public Properties getProperties() {
        return properties;
    }
}
