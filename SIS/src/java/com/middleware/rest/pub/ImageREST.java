package com.middleware.rest.pub;

import com.middleware.jersey.OAuthServerRequest;
import static com.middleware.jersey.App.oap;
import com.middleware.model.ImageContainer;
import com.middleware.model.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.oauth1.signature.OAuth1Parameters;
import org.glassfish.jersey.server.oauth1.OAuth1Token;

@Path("/public/image")
public class ImageREST {

    private EntityManagerFactory factory;
    private EntityManager em;

    public ImageREST() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@Context ContainerRequestContext containerRequestContext) {

        // If here the validity of the supplied Oauth parameters has been checked (by jersey oauth1)
        OAuthServerRequest oAuthServerRequest = new OAuthServerRequest(containerRequestContext);
        OAuth1Parameters oAuth1Parameters = new OAuth1Parameters();
        oAuth1Parameters.readRequest(oAuthServerRequest);
        String token = oAuth1Parameters.getToken();

        OAuth1Token accessToken = oap.getAccessToken(token);
        if (accessToken == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Get the user id associated with the token
        Integer uid = Integer.parseInt(accessToken.getPrincipal().getName());

        em.getTransaction().begin();

        if (em.find(User.class, uid) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        TypedQuery<com.middleware.model.Image> query
                = em.createNamedQuery("Image.findByUser", com.middleware.model.Image.class);
        query.setParameter("user", uid);
        List<com.middleware.model.Image> listImages = query.getResultList();
        em.getTransaction().commit();

        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET")
                .header("Access-Control-Max-Age", "1209600")
                .type(MediaType.APPLICATION_JSON)
                .entity(new ImageContainer(listImages))
                .build();
        }

}
