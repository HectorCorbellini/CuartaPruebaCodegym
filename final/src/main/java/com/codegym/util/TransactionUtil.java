package com.codegym.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.function.Function;

public class TransactionUtil {
    
    public static <T> T executeInTransaction(SessionFactory sessionFactory, Function<Session, T> operation) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            T result = operation.apply(session);
            session.getTransaction().commit();
            return result;
        }
    }
} 