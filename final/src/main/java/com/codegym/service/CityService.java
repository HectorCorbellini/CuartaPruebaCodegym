package com.codegym.service;

import com.codegym.dao.ICityRepository;
import com.codegym.dto.CityDTO;
import com.codegym.util.CacheKeyUtil;
import com.codegym.util.Constants;
import com.codegym.util.RedisConnectionManager;
import com.codegym.util.TransactionUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
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
        String cacheKey = CacheKeyUtil.getPaginatedCitiesKey(offset, limit);
        
        try (RedisConnectionManager redisManager = new RedisConnectionManager(redisClient)) {
            RedisCommands<String, String> redis = redisManager.sync();
            
            // Try to get from Redis first
            String cachedData = redis.get(cacheKey);
            if (cachedData != null) {
                return objectMapper.readValue(cachedData, new TypeReference<List<CityDTO>>() {});
            }
            
            // If not in cache, get from database
            List<CityDTO> cities = TransactionUtil.executeInTransaction(sessionFactory, session -> {
                List<CityDTO> result = CityDTO.fromEntities(
                    cityRepository.findAllPaginated(offset, limit)
                );
                
                try {
                    // Cache the result
                    redis.setex(
                        cacheKey,
                        CacheKeyUtil.CACHE_EXPIRATION_SECONDS,
                        objectMapper.writeValueAsString(result)
                    );
                } catch (Exception e) {
                    System.err.println("Error caching cities: " + e.getMessage());
                }
                
                return result;
            });
            
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
            
            List<CityDTO> cities = TransactionUtil.executeInTransaction(sessionFactory, session -> 
                CityDTO.fromEntities(cityRepository.findAllPaginated(offset, limit))
            );
            
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
            return TransactionUtil.executeInTransaction(sessionFactory, session -> 
                CityDTO.fromEntities(cityRepository.findByPopulationRange(minPopulation, maxPopulation))
            );
        } catch (Exception e) {
            System.err.println("Error getting cities by population range: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, List<CityDTO>> getCitiesByCategory(int limit) {
        try {
            List<CityDTO> cities = TransactionUtil.executeInTransaction(sessionFactory, session -> 
                CityDTO.fromEntities(cityRepository.findAllPaginated(Constants.DEFAULT_OFFSET, limit))
            );
            
            return cities.stream()
                .collect(Collectors.groupingBy(CityDTO::getPopulationCategory));
        } catch (Exception e) {
            System.err.println("Error grouping cities by category: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}
