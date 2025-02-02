package com.codegym.config;

import com.codegym.dao.CityDAO;
import com.codegym.domain.City;
import com.codegym.domain.Country;
import com.codegym.domain.CountryLanguage;
import com.codegym.service.CityService;
import com.codegym.service.ICityService;
import io.lettuce.core.RedisClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Main application configuration class.
 * Handles initialization of all application components and dependencies.
 */
public class AppConfig {
    private final DatabaseConfig databaseConfig;
    private final RedisConfig redisConfig;
    private SessionFactory sessionFactory;
    private RedisClient redisClient;
    private ICityService cityService;
    
    public AppConfig() {
        this.databaseConfig = new DatabaseConfig();
        this.redisConfig = new RedisConfig();
    }
    
    public void initialize() {
        this.sessionFactory = createSessionFactory();
        this.redisClient = redisConfig.createClient();
        this.cityService = createCityService();
    }
    
    private SessionFactory createSessionFactory() {
        return new Configuration()
            .addProperties(databaseConfig.getProperties())
            .addAnnotatedClass(City.class)
            .addAnnotatedClass(Country.class)
            .addAnnotatedClass(CountryLanguage.class)
            .buildSessionFactory();
    }
    
    private ICityService createCityService() {
        CityDAO cityDAO = new CityDAO(sessionFactory);
        return new CityService(cityDAO, sessionFactory, redisClient);
    }
    
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
    
    public ICityService getCityService() {
        return cityService;
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public RedisClient getRedisClient() {
        return redisClient;
    }
}
