package com.middleware.api;

import com.middleware.model.Uploadsession;
import javax.persistence.EntityManager;

public class API {
    

    public static void updateStatus(EntityManager em, String usid, String status) throws Exception {
        
        em.getTransaction().begin();
        Uploadsession us = em.find(Uploadsession.class, Integer.parseInt(usid));
        us.setStatus(Integer.parseInt(status));
        em.getTransaction().commit();
 
    }

    public static void setFinalURL(EntityManager em, String usid, String finalURL) throws Exception {
        em.getTransaction().begin();
        Uploadsession us = em.find(Uploadsession.class, Integer.parseInt(usid));
        us.setResult(finalURL);
        em.getTransaction().commit();
    }

}
