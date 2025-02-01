package com.codegym.util;

public class ValidationUtil {
    private static final int MAX_PAGE_SIZE = 1000;
    private static final int MIN_POPULATION = 0;
    private static final int MAX_POPULATION = 50_000_000; // Reasonable upper limit for city population

    private ValidationUtil() {
        // Utility class should not be instantiated
    }

    public static void validatePagination(int offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative, got: " + offset);
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive, got: " + limit);
        }
        if (limit > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Limit cannot exceed " + MAX_PAGE_SIZE + ", got: " + limit);
        }
    }
    
    public static void validatePopulationRange(int minPopulation, int maxPopulation) {
        if (minPopulation < MIN_POPULATION) {
            throw new IllegalArgumentException("Minimum population cannot be negative, got: " + minPopulation);
        }
        if (maxPopulation < minPopulation) {
            throw new IllegalArgumentException("Maximum population must be greater than or equal to minimum population, got min: " + 
                minPopulation + ", max: " + maxPopulation);
        }
        if (maxPopulation > MAX_POPULATION) {
            throw new IllegalArgumentException("Maximum population cannot exceed " + MAX_POPULATION + ", got: " + maxPopulation);
        }
    }
} 