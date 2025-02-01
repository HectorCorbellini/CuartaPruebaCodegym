package com.codegym.service;

import com.codegym.dao.ICityRepository;
import com.codegym.dto.CityDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ICityService.
 * Handles business logic for city operations.
 */
public class CityService implements ICityService {
    private final ICityRepository cityRepository;
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;

    public CityService(ICityRepository cityRepository, SessionFactory sessionFactory, RedisClient redisClient) {
        this.cityRepository = cityRepository;
        this.sessionFactory = sessionFactory;
        this.redisClient = redisClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<CityDTO> getPaginatedCities(int offset, int limit) {
        String cacheKey = String.format("cities:paginated:%d:%d", offset, limit);
        
        try {
            // Try to get from Redis first
            String cachedData = redisClient.connect().sync().get(cacheKey);
            if (cachedData != null) {
                return objectMapper.readValue(cachedData, new TypeReference<List<CityDTO>>() {});
            }
            
            // If not in cache, get from database
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            
            List<CityDTO> cities = CityDTO.fromEntities(
                cityRepository.findAllPaginated(offset, limit)
            );
            
            session.getTransaction().commit();
            
            // Cache the result
            redisClient.connect().sync().setex(cacheKey, 300, objectMapper.writeValueAsString(cities));
            
            return cities;
        } catch (Exception e) {
            System.err.println("Error getting paginated cities: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map.Entry<List<CityDTO>, Long> getPaginatedCitiesNoCache(int offset, int limit) {
        try {
            long startTime = System.currentTimeMillis();
            
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            
            List<CityDTO> cities = CityDTO.fromEntities(
                cityRepository.findAllPaginated(offset, limit)
            );
            
            session.getTransaction().commit();
            
            long timeTaken = System.currentTimeMillis() - startTime;
            return new AbstractMap.SimpleEntry<>(cities, timeTaken);
        } catch (Exception e) {
            System.err.println("Error getting paginated cities without cache: " + e.getMessage());
            return new AbstractMap.SimpleEntry<>(Collections.emptyList(), 0L);
        }
    }

    @Override
    public List<CityDTO> getCitiesByPopulationRange(int minPopulation, int maxPopulation) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            
            List<CityDTO> cities = CityDTO.fromEntities(
                cityRepository.findByPopulationRange(minPopulation, maxPopulation)
            );
            
            session.getTransaction().commit();
            return cities;
        } catch (Exception e) {
            System.err.println("Error getting cities by population range: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, List<CityDTO>> getCitiesByCategory(int limit) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            
            List<CityDTO> cities = CityDTO.fromEntities(
                cityRepository.findAllPaginated(0, limit)
            );
            
            session.getTransaction().commit();
            
            return cities.stream()
                .collect(Collectors.groupingBy(CityDTO::getPopulationCategory));
        } catch (Exception e) {
            System.err.println("Error grouping cities by category: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}
