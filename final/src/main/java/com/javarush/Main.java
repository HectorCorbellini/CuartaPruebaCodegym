package com.javarush;

import com.codegym.dao.CityDAO;
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

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Main application class demonstrating city data operations and transformations.
 */
public class Main {
    private static final int BATCH_SIZE = 500;

    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ICityService cityService;

    public Main() {
        checkAndHandlePortsInUse();
        this.sessionFactory = prepareRelationalDb();
        this.redisClient = prepareRedisClient();
        this.cityService = new CityService(new CityDAO(sessionFactory), sessionFactory);
    }

    private void showMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nCity Data Operations Menu:");
            System.out.println("1. View Cities (Pagination)");
            System.out.println("2. Find Cities by Population Range");
            System.out.println("3. View Cities by Category");
            System.out.println("4. Exit");
            System.out.print("Enter your choice (1-4): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    handlePagination(scanner);
                    break;
                case 2:
                    handlePopulationRange(scanner);
                    break;
                case 3:
                    handleCityCategories();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        // Cleanup
        shutdown();
    }

    private void handlePagination(Scanner scanner) {
        System.out.print("Enter offset (starting position): ");
        int offset = scanner.nextInt();
        System.out.print("Enter limit (number of cities to show): ");
        int limit = scanner.nextInt();

        System.out.println("\nValidating pagination parameters - offset: " + offset + ", limit: " + limit);
        List<CityDTO> cities = cityService.getPaginatedCities(offset, limit);
        System.out.println("Found " + cities.size() + " cities\n");

        for (CityDTO city : cities) {
            System.out.printf("City: %s%n", city.toString());
        }
    }

    private void handlePopulationRange(Scanner scanner) {
        System.out.print("Enter minimum population: ");
        int minPopulation = scanner.nextInt();
        System.out.print("Enter maximum population: ");
        int maxPopulation = scanner.nextInt();

        System.out.println("\nValidating population range - min: " + minPopulation + ", max: " + maxPopulation);
        List<CityDTO> cities = cityService.getCitiesByPopulationRange(minPopulation, maxPopulation);
        System.out.println("Found " + cities.size() + " cities in population range\n");

        for (CityDTO city : cities) {
            System.out.printf("City: %s%n", city.toString());
        }
    }

    private void handleCityCategories() {
        Map<String, List<CityDTO>> categorizedCities = cityService.getCitiesByCategory(1000); // Get up to 1000 cities

        System.out.println("\nCities by category:");
        for (Map.Entry<String, List<CityDTO>> entry : categorizedCities.entrySet()) {
            System.out.printf("%s Cities (%d)%n", entry.getKey(), entry.getValue().size());
            // Show first 5 cities of each category as examples
            entry.getValue().stream().limit(5).forEach(city ->
                System.out.printf("City: %s%n", city.toString()));
        }
    }

    private void checkAndHandlePortsInUse() {
        List<Integer> portsToCheck = new ArrayList<>();
        portsToCheck.add(3306); // MySQL port
        portsToCheck.add(6379); // Redis port

        for (int port : portsToCheck) {
            if (isPortInUse(port)) {
                System.out.println("Port " + port + " is already in use.");
                try {
                    killProcessOnPort(port);
                    System.out.println("Successfully killed process on port " + port);
                } catch (IOException | InterruptedException e) {
                    System.err.println("Failed to kill process on port " + port + ": " + e.getMessage());
                }
            }
        }
    }

    private boolean isPortInUse(int port) {
        try (Socket socket = new Socket("localhost", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void killProcessOnPort(int port) throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;
        
        if (os.contains("win")) {
            processBuilder = new ProcessBuilder("cmd", "/c", "netstat -ano | findstr :" + port);
        } else {
            processBuilder = new ProcessBuilder("sh", "-c", "lsof -i :" + port + " -t");
        }

        Process process = processBuilder.start();
        process.waitFor();

        if (process.exitValue() == 0) {
            String pid = new String(process.getInputStream().readAllBytes()).trim();
            if (!pid.isEmpty()) {
                ProcessBuilder killBuilder;
                if (os.contains("win")) {
                    killBuilder = new ProcessBuilder("taskkill", "/F", "/PID", pid);
                } else {
                    killBuilder = new ProcessBuilder("kill", "-9", pid);
                }
                killBuilder.start().waitFor();
            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.showMenu();
    }

    private SessionFactory prepareRelationalDb() {
        Configuration configuration = new Configuration();
        Properties properties = createDatabaseProperties();
        
        configuration.addProperties(properties);
        configuration.addAnnotatedClass(City.class);
        configuration.addAnnotatedClass(Country.class);
        configuration.addAnnotatedClass(CountryLanguage.class);
        
        return configuration.buildSessionFactory();
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
        properties.put(Environment.STATEMENT_BATCH_SIZE, BATCH_SIZE);
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