package com.middleware.jersey;

import com.middleware.model.Consumer;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MultivaluedHashMap;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1ServerFeature;

public class App extends ResourceConfig {

    public static DefaultOAuth1Provider oap;

    public App() {

        super();

        // On App bootstrap setup the OAuth1 Server
        
        oap = new DefaultOAuth1Provider();
        
        Feature oaFeature = new OAuth1ServerFeature(oap, "oauth1/request_token", "oauth1/access_token");

        // Retrieve Oauth1 consumers from the DB
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("RESTPU");
        EntityManager em = factory.createEntityManager();

        em.getTransaction().begin();
        TypedQuery<Consumer> query
                = em.createNamedQuery("Consumer.findAll", Consumer.class);

        List<Consumer> listConsumers = query.getResultList();
        em.getTransaction().commit();

        for (Consumer c : listConsumers) {
            oap.registerConsumer(c.getId().toString(),
                    c.getOauthConsumerKey(),
                    c.getOauthSignature(),
                    new MultivaluedHashMap<String, String>());
        }

        register(oaFeature);

    }

}
