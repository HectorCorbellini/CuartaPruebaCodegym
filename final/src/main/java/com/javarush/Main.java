package com.javarush;

import com.codegym.config.AppConfig;
import com.codegym.dto.CityDTO;
import com.codegym.service.ICityService;
import com.codegym.util.Constants;
import com.javarush.menu.MenuOption;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class demonstrating city data operations and transformations.
 */
public class Main {
    private static final int BATCH_SIZE = Constants.DEFAULT_BATCH_SIZE;

    private final AppConfig appConfig;
    private final ICityService cityService;

    public Main() {
        checkAndHandlePortsInUse();
        this.appConfig = new AppConfig();
        this.appConfig.initialize();
        this.cityService = appConfig.getCityService();
    }

    private void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nCity Data Operations Menu:");
            // Display menu options
            for (MenuOption option : MenuOption.values()) {
                System.out.printf("%d. %s%n", option.getValue(), option.getDisplayText());
            }
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                MenuOption selectedOption = MenuOption.fromValue(choice);
                
                if (selectedOption == null) {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }
                
                switch (selectedOption) {
                    case PAGINATED_CITIES:
                        handlePagination(scanner);
                        break;
                    case POPULATION_RANGE:
                        handlePopulationRange(scanner);
                        break;
                    case CITY_CATEGORIES:
                        handleCityCategories();
                        break;
                    case PERFORMANCE_COMPARISON:
                        handlePerformanceComparison(scanner);
                        break;
                    case EXIT:
                        System.out.println("Exiting...");
                        shutdown();
                        return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();
        }
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

    private void handlePerformanceComparison(Scanner scanner) {
        System.out.println("\nComparing Redis Cache vs Direct Database Access");
        System.out.print("Enter offset (0 or greater): ");
        int offset = scanner.nextInt();
        System.out.print("Enter limit (1 or greater): ");
        int limit = scanner.nextInt();

        // First run with cache
        long startTime = System.currentTimeMillis();
        List<CityDTO> cachedCities = cityService.getPaginatedCities(offset, limit);
        long cacheTime = System.currentTimeMillis() - startTime;

        // Then run without cache
        Map.Entry<List<CityDTO>, Long> dbResult = cityService.getPaginatedCitiesNoCache(offset, limit);
        List<CityDTO> dbCities = dbResult.getKey();
        long dbTime = dbResult.getValue();

        // Print results
        System.out.println("\nPerformance Results:");
        System.out.printf("Redis Cache: %d ms (Retrieved %d cities)%n", cacheTime, cachedCities.size());
        System.out.printf("Direct DB Access: %d ms (Retrieved %d cities)%n", dbTime, dbCities.size());
        System.out.printf("Difference: %d ms (Cache is %.2fx %s)%n", 
            Math.abs(cacheTime - dbTime),
            dbTime > cacheTime ? (double)dbTime/cacheTime : (double)cacheTime/dbTime,
            dbTime > cacheTime ? "faster" : "slower");
    }

    private void handleCityCategories() {
        Map<String, List<CityDTO>> categorizedCities = cityService.getCitiesByCategory(Constants.DEFAULT_CATEGORY_LIMIT);

        System.out.println("\nCities by category:");
        for (Map.Entry<String, List<CityDTO>> entry : categorizedCities.entrySet()) {
            System.out.printf("%s Cities (%d)%n", entry.getKey(), entry.getValue().size());
            // Show first few cities of each category as examples
            entry.getValue().stream().limit(Constants.EXAMPLE_DISPLAY_LIMIT).forEach(city ->
                System.out.printf("City: %s%n", city.toString()));
        }
    }

    private void checkAndHandlePortsInUse() {
        List<Integer> portsToCheck = new ArrayList<>();
        portsToCheck.add(Constants.MYSQL_PORT);
        portsToCheck.add(Constants.REDIS_PORT);

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



    private void shutdown() {
        if (appConfig != null) {
            appConfig.shutdown();
        }
    }
}