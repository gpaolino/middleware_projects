package com.middleware.rest.priv;

import com.middleware.model.Consumer;
import com.middleware.session.SessionTools;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/private/app")
public class AppREST {

    @Context
    UriInfo uriInfo;

    private final EntityManagerFactory factory;
    private EntityManager em;

    public AppREST() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @GET
    @Path("{appid}")
    public Response get(
            @PathParam("appid") String appid,
            MultivaluedMap<String, String> params,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Integer uid = SessionTools.checkSession(token);

        if (uid != null) {

            em.getTransaction().begin();
            Consumer c = em.find(Consumer.class, appid);
            em.getTransaction().commit();

            if (c == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            if (c.getUser() != uid.intValue()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            return Response.ok(c, MediaType.APPLICATION_JSON).build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

}
