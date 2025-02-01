package com.javarush;

import com.codegym.dao.CityDAO;
import com.codegym.dao.CountryDAO;
import com.codegym.domain.City;
import com.codegym.domain.Country;
import com.codegym.domain.CountryLanguage;
import com.codegym.dto.CityDTO;
import com.codegym.service.CityService;
import com.codegym.service.ICityService;
import io.lettuce.core.RedisClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Main application class demonstrating city data operations and transformations.
 */
public class Main {
    private static final int BATCH_SIZE = 500;

    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ICityService cityService;

    public Main() {
        this.sessionFactory = prepareRelationalDb();
        this.redisClient = prepareRedisClient();
        this.cityService = new CityService(new CityDAO(sessionFactory), sessionFactory);
    }

    public static void main(String[] args) {
        Main main = new Main();
        
        try {
            System.out.println("Starting city data demonstration");
            
            // Example 1: Fetch and transform all cities
            List<CityDTO> allCityDTOs = main.cityService.getPaginatedCities(0, 100);
            System.out.println("Transformed " + allCityDTOs.size() + " cities");
            allCityDTOs.forEach(dto -> System.out.println(dto.toString()));

            // Example 2: Filter cities by population
            List<CityDTO> largeCities = main.cityService.getCitiesByPopulationRange(1_000_000, 5_000_000);
            System.out.println("\nLarge cities (population >= 1M): " + largeCities.size());
            largeCities.forEach(dto -> System.out.println(dto.toString()));

            // Example 3: Group cities by category
            Map<String, List<CityDTO>> citiesByCategory = main.cityService.getCitiesByCategory(1000);
            System.out.println("\nCities by category:");
            citiesByCategory.forEach((category, categoryCities) -> {
                System.out.println(category + " Cities (" + categoryCities.size() + ")");
                categoryCities.stream()
                    .limit(5)
                    .forEach(dto -> System.out.println(dto.toString()));
            });

        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        } finally {
            main.shutdown();
            System.out.println("Application shutdown complete");
        }
    }

    private SessionFactory prepareRelationalDb() {
        return new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .setProperties(createDatabaseProperties())
                .buildSessionFactory();
    }

    private Properties createDatabaseProperties() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, System.getenv().getOrDefault("MYSQL_URL", "jdbc:mysql://localhost:3306/world"));
        properties.put(Environment.USER, System.getenv().getOrDefault("MYSQL_USER", "hibernate_user"));
        properties.put(Environment.PASS, System.getenv().getOrDefault("MYSQL_PASSWORD", "hibernate_password"));
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.SHOW_SQL, "false");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        return properties;
    }

    private RedisClient prepareRedisClient() {
        String redisUrl = System.getenv().getOrDefault("REDIS_URL", "redis://localhost:6379/0");
        return RedisClient.create(redisUrl);
    }

    private void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}