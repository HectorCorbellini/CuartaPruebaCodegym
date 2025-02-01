package com.codegym.dao;

import com.codegym.domain.City;
import com.codegym.util.ValidationUtil;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CityDAO {
    private final SessionFactory sessionFactory;

    public CityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<City> findAllPaginated(int offset, int limit) {
        ValidationUtil.validatePagination(offset, limit);
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public int countAll() {
        Query<Long> query = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class);
        return Math.toIntExact(query.uniqueResult());
    }

    public List<City> findByPopulationRange(int minPopulation, int maxPopulation) {
        ValidationUtil.validatePopulationRange(minPopulation, maxPopulation);
        Query<City> query = sessionFactory.getCurrentSession()
            .createQuery("select c from City c where c.population between :minPop and :maxPop order by c.population desc", City.class);
        query.setParameter("minPop", minPopulation);
        query.setParameter("maxPop", maxPopulation);
        return query.list();
    }
}