package com.codegym.dao;

import com.codegym.domain.City;
import com.codegym.util.ValidationUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for City entities.
 * Handles database operations for City data.
 */
public class CityDAO implements ICityRepository {
    private static final String FIND_ALL_QUERY = "SELECT c FROM City c";
    private static final String COUNT_ALL_QUERY = "SELECT COUNT(c) FROM City c";
    private static final String FIND_BY_POPULATION_RANGE_QUERY = 
        "SELECT c FROM City c WHERE c.population BETWEEN :minPop AND :maxPop ORDER BY c.population DESC";

    private final SessionFactory sessionFactory;

    /**
     * Constructs a new CityDAO.
     * @param sessionFactory Hibernate SessionFactory for database operations
     * @throws IllegalArgumentException if sessionFactory is null
     */
    public CityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = Optional.ofNullable(sessionFactory)
            .orElseThrow(() -> new IllegalArgumentException("SessionFactory cannot be null"));
    }

    @Override
    public List<City> findAllPaginated(int offset, int limit) {
        try {
            ValidationUtil.validatePagination(offset, limit);
            System.out.println("Fetching cities with offset " + offset + " and limit " + limit);

            Session session = sessionFactory.getCurrentSession();
            Query<City> query = session.createQuery(FIND_ALL_QUERY, City.class);
            query.setFirstResult(offset);
            query.setMaxResults(limit);

            List<City> results = query.list();
            System.out.println("Found " + results.size() + " cities");
            return results;

        } catch (Exception e) {
            System.err.println("Failed to fetch paginated cities: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public int countAll() {
        try {
            System.out.println("Counting all cities");
            Session session = sessionFactory.getCurrentSession();
            Query<Long> query = session.createQuery(COUNT_ALL_QUERY, Long.class);
            
            int count = Math.toIntExact(query.uniqueResult());
            System.out.println("Total city count: " + count);
            return count;

        } catch (Exception e) {
            System.err.println("Failed to count cities: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public List<City> findByPopulationRange(int minPopulation, int maxPopulation) {
        try {
            ValidationUtil.validatePopulationRange(minPopulation, maxPopulation);
            System.out.println("Finding cities with population between " + minPopulation + " and " + maxPopulation);

            Session session = sessionFactory.getCurrentSession();
            Query<City> query = session.createQuery(FIND_BY_POPULATION_RANGE_QUERY, City.class);
            query.setParameter("minPop", minPopulation);
            query.setParameter("maxPop", maxPopulation);

            List<City> results = query.list();
            System.out.println("Found " + results.size() + " cities in population range");
            return results;

        } catch (Exception e) {
            System.err.println("Failed to find cities by population range: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}