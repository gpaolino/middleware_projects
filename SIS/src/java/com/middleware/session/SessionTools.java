package com.middleware.session;

import com.middleware.model.Session;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;


public class SessionTools {

    private static SecureRandom random = new SecureRandom();

    public static String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    public static Date getExpiration() {
        long theFuture = System.currentTimeMillis() + (86400 * 1 * 1000);
        Date tomorrow = new Date(theFuture);
        return tomorrow;
    }

    public static Date currentDate() {
        return new Date();
    }
    
    public static boolean checkSession(String token, Integer userID) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("RESTPU");;
        EntityManager em = factory.createEntityManager();

        // Try to get session with token passed as parameter
        TypedQuery<Session> query
                = em.createNamedQuery("Session.isValidForUser", Session.class);
        query.setParameter("token", token);
        query.setParameter("user", userID);
        query.setParameter("now", new Date());

        List<Session> listSessions = query.getResultList();

        if (listSessions.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }

    // returns null if the session is not valid
    // returns the userID otherwise
    public static Integer checkSession(String token) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("RESTPU");;
        EntityManager em = factory.createEntityManager();

        // Try to get session with token passed as parameter
        TypedQuery<Session> query
                = em.createNamedQuery("Session.isValid", Session.class);
        query.setParameter("token", token);
        query.setParameter("now", new Date());

        List<Session> listSessions = query.getResultList();

        if (listSessions.isEmpty()) {
            return null;
        } else {
            return listSessions.get(0).getUser();
        }

    }

}
