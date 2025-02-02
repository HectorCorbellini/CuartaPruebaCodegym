package com.codegym.util;

/**
 * Utility class for validating input parameters.
 * Contains validation logic for pagination and population ranges.
 */
public final class ValidationUtil {
    // Pagination constants
    private static final int MAX_PAGE_SIZE = 1000;
    private static final int MIN_OFFSET = 0;
    private static final int MIN_LIMIT = 1;

    // Population constants
    private static final int MIN_POPULATION = 0;
    private static final int MAX_POPULATION = 50_000_000; // Current largest city (Shanghai) ~27M

    private ValidationUtil() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Validates pagination parameters.
     * @param offset Starting position for pagination (must be >= 0)
     * @param limit Maximum number of results to return (must be > 0 and <= MAX_PAGE_SIZE)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public static void validatePagination(int offset, int limit) {
        System.out.println("Validating pagination parameters - offset: " + offset + ", limit: " + limit);

        if (offset < MIN_OFFSET) {
            String message = String.format("Offset cannot be negative, got: %d", offset);
            System.err.println(message);
            throw new IllegalArgumentException(message);
        }

        if (limit < MIN_LIMIT) {
            String message = String.format("Limit must be positive, got: %d", limit);
            System.err.println(message);
            throw new IllegalArgumentException(message);
        }

        if (limit > MAX_PAGE_SIZE) {
            String message = String.format("Limit cannot exceed %d, got: %d", MAX_PAGE_SIZE, limit);
            System.err.println(message);
            throw new IllegalArgumentException(message);
        }

        System.out.println("Pagination parameters are valid");
    }
    
    /**
     * Validates population range parameters.
     * @param minPopulation Minimum population (must be >= 0)
     * @param maxPopulation Maximum population (must be >= minPopulation and <= MAX_POPULATION)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public static void validatePopulationRange(int minPopulation, int maxPopulation) {
        System.out.println("Validating population range - min: " + minPopulation + ", max: " + maxPopulation);

        if (minPopulation < MIN_POPULATION) {
            String message = String.format("Minimum population cannot be negative, got: %d", minPopulation);
            System.err.println(message);
            throw new IllegalArgumentException(message);
        }

        if (maxPopulation < minPopulation) {
            String message = String.format(
                "Maximum population must be greater than or equal to minimum population, got min: %d, max: %d",
                minPopulation, maxPopulation);
            System.err.println(message);
            throw new IllegalArgumentException(message);
        }

        if (maxPopulation > MAX_POPULATION) {
            String message = String.format("Maximum population cannot exceed %d, got: %d", MAX_POPULATION, maxPopulation);
            System.err.println(message);
            throw new IllegalArgumentException(message);
        }

        System.out.println("Population range parameters are valid");
    }
}