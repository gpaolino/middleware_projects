package com.middleware.rest.priv;

import com.middleware.jersey.App;
import com.middleware.model.Consumer;
import java.security.Principal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;

@Path("/private/consumer")
public class ConsumerREST {

    @Context
    UriInfo uriInfo;

    private EntityManagerFactory factory;
    private EntityManager em;

    public ConsumerREST() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }
    
    @GET
    @Path("{token}/test") 
    public String testToken( @PathParam("token") String token) {
    
        return App.oap.getAccessToken(token).getPrincipal().getName();
       
    
    }

    @GET
    @Path("{oauth_token}/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConsumerName(
            @PathParam("oauth_token") String oauth_token,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (SessionREST.checkSession(token) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        DefaultOAuth1Provider.Token requestToken = App.oap.getRequestToken(oauth_token);

        if (requestToken == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String consumerKey = requestToken.getConsumer().getKey();

        em.getTransaction().begin();
        TypedQuery<Consumer> query
                = em.createNamedQuery("Consumer.findByOauthConsumerKey", Consumer.class);

        query.setParameter("oauthConsumerKey", consumerKey);

        List<Consumer> listConsumers = query.getResultList();
        em.getTransaction().commit();

        String name = listConsumers.get(0).getAppName();
        String answ = "{\"appName\":\"" + name + "\"}";

        return Response.ok(answ, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("{oauth_token}/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorize(
            @PathParam("oauth_token") String oauth_token,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Integer uid = SessionREST.checkSession(token);
        if (uid == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        DefaultOAuth1Provider.Token requestToken = App.oap.getRequestToken(oauth_token);

        if (requestToken == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String verifier = App.oap.authorizeToken(requestToken, new Principal() {
            public String getName() {
                return uid.toString();
            }
        }, null);

        String answ = "{\"verifier\":\"" + verifier + "\"}";

        return Response.ok(answ, MediaType.APPLICATION_JSON).build();
    }

}
