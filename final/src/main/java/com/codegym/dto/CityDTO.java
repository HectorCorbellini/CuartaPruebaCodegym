package com.codegym.dto;

import com.codegym.domain.City;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for City entities.
 * Provides transformation and filtering capabilities for city data.
 */
public class CityDTO implements Serializable {
    private static final int SMALL_CITY_THRESHOLD = 100_000;
    private static final int MEDIUM_CITY_THRESHOLD = 500_000;
    private static final int LARGE_CITY_THRESHOLD = 1_000_000;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("district")
    private final String district;

    @JsonProperty("population")
    private final int population;

    @JsonProperty("population_category")
    private final String populationCategory;

    private CityDTO(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "City name cannot be null");
        this.district = Objects.requireNonNull(builder.district, "District cannot be null");
        this.population = builder.population;
        this.populationCategory = calculatePopulationCategory(builder.population);
    }

    /**
     * Calculates the population category based on city size.
     * @param population The city's population
     * @return Population category (Small, Medium, Large, or Metropolis)
     */
    private static String calculatePopulationCategory(int population) {
        if (population < 0) {
            throw new IllegalArgumentException("Population cannot be negative");
        }
        if (population < SMALL_CITY_THRESHOLD) return "Small";
        if (population < MEDIUM_CITY_THRESHOLD) return "Medium";
        if (population < LARGE_CITY_THRESHOLD) return "Large";
        return "Metropolis";
    }

    /**
     * Creates a CityDTO from a City entity.
     * @param city The source City entity
     * @return A new CityDTO instance
     * @throws IllegalArgumentException if city is null
     */
    public static CityDTO fromEntity(City city) {
        Objects.requireNonNull(city, "City entity cannot be null");
        return new Builder()
                .withName(city.getName())
                .withDistrict(city.getDistrict())
                .withPopulation(city.getPopulation())
                .build();
    }

    /**
     * Transforms a list of City entities into CityDTOs.
     * @param cities List of City entities
     * @return List of CityDTOs
     * @throws IllegalArgumentException if cities list is null
     */
    public static List<CityDTO> fromEntities(List<City> cities) {
        Objects.requireNonNull(cities, "Cities list cannot be null");
        return cities.stream()
                .map(CityDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Transforms and filters cities based on minimum population.
     * @param cities List of City entities
     * @param minPopulation Minimum population threshold
     * @return Filtered and sorted list of CityDTOs
     * @throws IllegalArgumentException if cities list is null or minPopulation is negative
     */
    public static List<CityDTO> filterByMinPopulation(List<City> cities, int minPopulation) {
        Objects.requireNonNull(cities, "Cities list cannot be null");
        if (minPopulation < 0) {
            throw new IllegalArgumentException("Minimum population cannot be negative");
        }

        return cities.stream()
                .filter(city -> city.getPopulation() >= minPopulation)
                .map(CityDTO::fromEntity)
                .sorted((c1, c2) -> Integer.compare(c2.getPopulation(), c1.getPopulation()))
                .collect(Collectors.toList());
    }

    // Immutable getters
    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }

    public int getPopulation() {
        return population;
    }

    public String getPopulationCategory() {
        return populationCategory;
    }

    @Override
    public String toString() {
        return String.format("City: %s, District: %s, Population: %d, Category: %s",
                name, district, population, populationCategory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CityDTO)) return false;
        CityDTO cityDTO = (CityDTO) o;
        return population == cityDTO.population &&
                Objects.equals(name, cityDTO.name) &&
                Objects.equals(district, cityDTO.district);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, district, population);
    }

    /**
     * Builder for CityDTO.
     */
    public static class Builder {
        private String name;
        private String district;
        private int population;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDistrict(String district) {
            this.district = district;
            return this;
        }

        public Builder withPopulation(int population) {
            this.population = population;
            return this;
        }

        public CityDTO build() {
            return new CityDTO(this);
        }
    }
}
