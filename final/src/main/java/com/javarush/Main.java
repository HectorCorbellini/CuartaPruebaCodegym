package com.javarush;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.codegym.dao.CityDAO;
import com.codegym.dao.CountryDAO;
import com.codegym.domain.City;
import com.codegym.domain.Country;
import com.codegym.domain.CountryLanguage;
import com.codegym.util.TransactionUtil;
import io.lettuce.core.RedisClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Main {
    private static final int BATCH_SIZE = 500;

    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ObjectMapper mapper;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public Main() {
        this.sessionFactory = prepareRelationalDb();
        this.cityDAO = new CityDAO(sessionFactory);
        this.countryDAO = new CountryDAO(sessionFactory);
        this.redisClient = prepareRedisClient();
        this.mapper = new ObjectMapper();
    }

    public static void main(String[] args) {
        Main main = new Main();
        
        try {
            // Example 1: Fetch all cities
            System.out.println("Fetching all cities:");
            List<City> allCities = main.fetchAllCities();
            System.out.printf("Fetched %d cities%n", allCities.size());
            main.printCities(allCities);
            
            // Example 2: Fetch cities with population between 1M and 2M
            System.out.println("\nFetching cities with population between 1M and 2M:");
            List<City> citiesByPopulation = main.fetchCitiesByPopulationRange(1_000_000, 2_000_000);
            main.printCities(citiesByPopulation);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input parameters: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            main.shutdown();
        }
    }

    private void printCities(List<City> cities) {
        cities.forEach(city -> 
            System.out.printf("City: %s, District: %s, Population: %d%n", 
                city.getName(), 
                city.getDistrict(), 
                city.getPopulation())
        );
    }

    private void shutdown() {
        if (Objects.nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (Objects.nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }

    private static Properties loadEnvProperties() {
        Properties envProps = new Properties();
        try {
            File envFile = new File(".env");
            if (!envFile.exists()) {
                envFile = new File("final/.env");
            }
            if (envFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() || line.startsWith("#")) continue;
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            envProps.put(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading .env file: " + e.getMessage());
        }
        return envProps;
    }

    private Properties createDatabaseProperties() {
        Properties envProps = loadEnvProperties();
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, envProps.getProperty("MYSQL_URL", "jdbc:mysql://localhost:3306/world"));
        properties.put(Environment.USER, envProps.getProperty("MYSQL_USER", "hibernate_user"));
        properties.put(Environment.PASS, envProps.getProperty("MYSQL_PASSWORD", "hibernate_password"));
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "100");
        return properties;
    }

    private SessionFactory prepareRelationalDb() {
        return new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .addProperties(createDatabaseProperties())
                .buildSessionFactory();
    }

    private RedisClient prepareRedisClient() {
        return RedisClient.create("redis://localhost:6379");
    }

    private List<City> fetchAllCities() {
        try {
            return TransactionUtil.executeInTransaction(sessionFactory, session -> {
                List<City> allCities = new ArrayList<>();
                int totalCount = cityDAO.countAll();
                for (int i = 0; i < totalCount; i += BATCH_SIZE) {
                    allCities.addAll(cityDAO.findAllPaginated(i, BATCH_SIZE));
                }
                return allCities;
            });
        } catch (IllegalArgumentException e) {
            System.err.println("Error fetching cities: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<City> fetchCitiesByPopulationRange(int minPopulation, int maxPopulation) {
        try {
            return TransactionUtil.executeInTransaction(sessionFactory, session -> 
                cityDAO.findByPopulationRange(minPopulation, maxPopulation)
            );
        } catch (IllegalArgumentException e) {
            System.err.println("Error fetching cities by population range: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}