package com.codegym.service;

import com.codegym.dto.CityDTO;
import java.util.List;
import java.util.Map;

/**
 * Service interface for city operations.
 * Defines the business logic contract for city-related operations.
 */
public interface ICityService {
    /**
     * Gets a paginated list of city DTOs using Redis cache when possible.
     * @param offset Starting position
     * @param limit Maximum number of results
     * @return List of CityDTOs
     */
    List<CityDTO> getPaginatedCities(int offset, int limit);

    /**
     * Gets a paginated list of city DTOs directly from the database, bypassing cache.
     * @param offset Starting position
     * @param limit Maximum number of results
     * @return List of CityDTOs and the time taken in milliseconds
     */
    Map.Entry<List<CityDTO>, Long> getPaginatedCitiesNoCache(int offset, int limit);

    /**
     * Gets cities within a population range.
     * @param minPopulation Minimum population
     * @param maxPopulation Maximum population
     * @return List of CityDTOs
     */
    List<CityDTO> getCitiesByPopulationRange(int minPopulation, int maxPopulation);

    /**
     * Groups cities by their population category.
     * @param limit Maximum number of cities to process
     * @return Map of category to list of CityDTOs
     */
    Map<String, List<CityDTO>> getCitiesByCategory(int limit);
}
