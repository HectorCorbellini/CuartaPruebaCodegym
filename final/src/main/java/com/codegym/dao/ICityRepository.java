package com.codegym.dao;

import com.codegym.domain.City;
import java.util.List;

/**
 * Repository interface for City entities.
 * Defines the contract for city data access operations.
 */
public interface ICityRepository {
    /**
     * Retrieves a paginated list of cities.
     * @param offset Starting position
     * @param limit Maximum number of results
     * @return List of cities
     */
    List<City> findAllPaginated(int offset, int limit);

    /**
     * Counts total number of cities in the database.
     * @return Total number of cities
     */
    int countAll();

    /**
     * Finds cities within a specified population range.
     * @param minPopulation Minimum population (inclusive)
     * @param maxPopulation Maximum population (inclusive)
     * @return List of cities within the population range
     */
    List<City> findByPopulationRange(int minPopulation, int maxPopulation);
}
