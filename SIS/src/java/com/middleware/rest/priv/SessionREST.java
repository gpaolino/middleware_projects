package com.middleware.rest.priv;

import com.middleware.model.Session;
import com.middleware.model.User;
import com.middleware.session.SessionTools;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/private/session")
public class SessionREST {

    @Context
    UriInfo uriInfo;

    private EntityManagerFactory factory;
    private EntityManager em;

    public SessionREST() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @POST
    public Response login(MultivaluedMap<String, String> params) {

        User u = null;
        Session s = null;
        Response r = null;

        String email = params.getFirst("email");
        String password = params.getFirst("password");

        // Check for bad request
        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        em.getTransaction().begin();

        TypedQuery<User> query
                = em.createNamedQuery("User.findByEmail", User.class);

        query.setParameter("email", email);
        List<User> listUsers = query.getResultList();

        if (listUsers.isEmpty()) {
            // username not found
            r = Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            u = listUsers.get(0);

            if (u.getPassword().equals(password)) {

                // Check if a valid session is present
                TypedQuery<Session> query1
                        = em.createNamedQuery("Session.findValid", Session.class);

                query1.setParameter("user", u.getId());
                query1.setParameter("date", SessionTools.currentDate());

                List<Session> listSessions = query1.getResultList();
                if (listSessions.isEmpty()) {
                    // no valid session, create
                    s = new Session();
                    s.setUser(u.getId());
                    s.setToken(SessionTools.nextSessionId());
                    s.setExpiration(SessionTools.getExpiration());

                    em.persist(s);

                } else {
                    // return the existing session
                    s = listSessions.get(0);
                }

                r = Response.ok(s, MediaType.APPLICATION_JSON).build();

            } else {
                // wrong password
                r = Response.status(Response.Status.UNAUTHORIZED).build();
            }

        }

        em.getTransaction().commit();

        return r;
    }

    @DELETE
    public Response logout(@HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        em.getTransaction().begin();
        TypedQuery<Session> query
                = em.createNamedQuery("Session.findByToken", Session.class);

        query.setParameter("token", token);
        
        List<Session> listSessions = query.getResultList();

        
        
        if( listSessions.isEmpty() ) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } 
        
        Session s = listSessions.get(0);
        
        em.remove(s);
        
        em.getTransaction().commit();
        
        
        return Response.status(Response.Status.OK).build();

    }

}
