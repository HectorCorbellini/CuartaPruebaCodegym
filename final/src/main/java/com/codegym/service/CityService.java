package com.codegym.service;

import com.codegym.dao.ICityRepository;
import com.codegym.dto.CityDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

    public CityService(ICityRepository cityRepository, SessionFactory sessionFactory) {
        this.cityRepository = cityRepository;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<CityDTO> getPaginatedCities(int offset, int limit) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            
            List<CityDTO> cities = CityDTO.fromEntities(
                cityRepository.findAllPaginated(offset, limit)
            );
            
            session.getTransaction().commit();
            return cities;
        } catch (Exception e) {
            System.err.println("Error getting paginated cities: " + e.getMessage());
            return Collections.emptyList();
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
