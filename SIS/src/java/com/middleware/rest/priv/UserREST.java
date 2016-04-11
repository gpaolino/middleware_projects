package com.middleware.rest.priv;

import com.middleware.model.ImageContainer;
import com.middleware.dropbox.DropboxClient;
import static com.middleware.jersey.App.oap;
import com.middleware.model.Consumer;
import com.middleware.model.ConsumerContainer;
import com.middleware.model.User;
import com.middleware.model.Image;
import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/private/user")
public class UserREST {

    @Context
    UriInfo uriInfo;

    private EntityManagerFactory factory;
    private EntityManager em;
    
    private static SecureRandom random = new SecureRandom();

    public UserREST() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    // All users
    public List<User> all() {

        em.getTransaction().begin();
        TypedQuery<User> query
                = em.createNamedQuery("User.findAll", User.class);

        List<User> listUsers = query.getResultList();
        em.getTransaction().commit();

        return listUsers;
    }

    @GET
    @Path("{uid}/images/")
    // All images of a user
    public Response images(
            @PathParam("uid") String uid) {

        em.getTransaction().begin();

        if (em.find(User.class, Integer.parseInt(uid)) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        TypedQuery<Image> query
                = em.createNamedQuery("Image.findByUser", Image.class);
        query.setParameter("user", Integer.parseInt(uid));
        List<Image> listImages = query.getResultList();
        em.getTransaction().commit();

        return Response.ok(new ImageContainer(listImages), MediaType.APPLICATION_JSON).build();

    }

    @GET
    @Path("{uid}/dropbox/")
    public Response startAuthorizationProcess(
            @PathParam("uid") String id,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        em.getTransaction().begin();
        User user = em.find(User.class, Integer.parseInt(id));

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (SessionREST.checkSession(token, Integer.parseInt(id))) {
            User u = em.find(User.class, Integer.parseInt(id));
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        em.getTransaction().commit();

        String authURL = DropboxClient.getAuthorizationURL();

        user.setDropboxTemp(authURL);

        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();

        return Response.ok("{ \"auth_url\":\"" + authURL + "\"}", MediaType.APPLICATION_JSON).build();

    }

    @POST
    @Path("{uid}/dropbox/")
    public Response completeAuthorizationProcess(
            @PathParam("uid") String id,
            @HeaderParam("access-token") String token,
            MultivaluedMap<String, String> params) {

        if (token == null || params.getFirst("auth_code") == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        em.getTransaction().begin();
        User user = em.find(User.class, Integer.parseInt(id));

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (SessionREST.checkSession(token, Integer.parseInt(id))) {
            User u = em.find(User.class, Integer.parseInt(id));
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        em.getTransaction().commit();

        System.err.println(params.getFirst("auth_code"));
        String permaToken = DropboxClient.completeAuthorization(params.getFirst("auth_code"));

        user.setDropboxToken(permaToken);
        user.setPairedWithDropbox(true);

        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();

        return Response.status(Response.Status.OK).build();

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id) {

        em.getTransaction().begin();
        User user = em.find(User.class, Integer.parseInt(id));
        em.getTransaction().commit();

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {

            user.setPassword("##hidden##");
            return Response.ok(user, MediaType.APPLICATION_JSON).build();

        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(MultivaluedMap<String, String> params) {

        User u = new User();
        // u.setId(new Integer(108932));
        u.setEmail(params.getFirst("email"));
        u.setName(params.getFirst("name"));
        u.setPassword(params.getFirst("password"));

        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();

        //prepare the output for the POST 
        String uriString = uriInfo.getAbsolutePath().toString();
        uriString = uriString + "/" + u.getId();
        URI uri = URI.create(uriString);

        return Response.created(uri).build();

    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id, @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        em.getTransaction().begin();
        User user = em.find(User.class, Integer.parseInt(id));

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (SessionREST.checkSession(token, Integer.parseInt(id))) {
            User u = em.find(User.class, Integer.parseInt(id));
            em.remove(u);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        em.getTransaction().commit();

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response update(@PathParam("id") String uid, MultivaluedMap<String, String> params, @HeaderParam("access-token") String token) {

        Integer id = Integer.parseInt(uid);
        String email = params.getFirst("email");
        String name = params.getFirst("name");
        String password = params.getFirst("password");

        User u = null;

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (SessionREST.checkSession(token, id)) {
            em.getTransaction().begin();
            u = em.find(User.class, Integer.parseInt(uid));
            u.setId(id);
            u.setEmail(email);
            u.setName(name);
            u.setPassword(password);

            em.merge(u);
            em.getTransaction().commit();

        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok(u, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("{uid}/apps")
    public Response addApp(
            @PathParam("uid") String uid, 
            MultivaluedMap<String, String> params,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (SessionREST.checkSession(token, Integer.parseInt(uid) )) {

            
            Consumer c = new Consumer();
            c.setAppName(params.getFirst("name"));
            c.setOauthConsumerKey(new BigInteger(130, random).toString(32));
            c.setOauthSignature(new BigInteger(130, random).toString(32));
            c.setOauthCallback("-");
            c.setUser(Integer.parseInt(uid));

            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
            
            // Add to jersey oauth1 
            oap.registerConsumer(c.getId().toString(),
                c.getOauthConsumerKey(),
                c.getOauthSignature(),
                new MultivaluedHashMap<String, String>());

            //prepare the output for the POST 
            String uriString = uriInfo.getAbsolutePath().toString();
            uriString = uriString + "/" + uid + "/apps/" + c.getId();
            URI uri = URI.create(uriString);
            
            return Response.created(uri).build();
            
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }
    
    @GET
    @Path("/{uid}/apps/")
    public Response getApps(
            @PathParam("uid") String uid, 
            MultivaluedMap<String, String> params,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (SessionREST.checkSession(token, Integer.parseInt(uid) )) {

             em.getTransaction().begin();
             TypedQuery<Consumer> query
                = em.createNamedQuery("Consumer.findByUser", Consumer.class);
             query.setParameter("user", Integer.parseInt(uid));

            List<Consumer> listConsumers = query.getResultList();
            em.getTransaction().commit();

            return Response.ok(new ConsumerContainer(listConsumers), MediaType.APPLICATION_JSON).build();
            
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

    @GET
    @Path("{uid}/apps/{appid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApp(
            @PathParam("uid") String uid, 
            @PathParam("appid") String appid, 
            MultivaluedMap<String, String> params,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (SessionREST.checkSession(token, Integer.parseInt(uid) )) {

            em.getTransaction().begin();
            Consumer c =  em.find(Consumer.class, appid);
            em.getTransaction().commit();

            return Response.ok(c, MediaType.APPLICATION_JSON).build();
            
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

}