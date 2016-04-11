package com.middleware.rest.priv;

import com.middleware.jersey.App;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import static com.middleware.rest.priv.SessionREST.checkSession;

@Path("/private/oauth1/support")
public class OAuth1Support {

    private EntityManagerFactory factory;
    private EntityManager em;

    public OAuth1Support() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @GET
    @Path("{oauth_token}/info")
    public Response getAppName(@PathParam("oauth_token") String oauth_token) {

        DefaultOAuth1Provider.Token tok1 = App.oap.getRequestToken(oauth_token);

        if (tok1 == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            String answer = "{ \"consumer_name\": \"ciao\", \"callback_url\":\"ciao\"}";
            return Response.ok(answer, MediaType.APPLICATION_JSON).build();
        }

    }

    @GET
    @Path("{oauth_token}/authorize")
    public Response authorize(@PathParam("oauth_token") String oauth_token, @HeaderParam("access-token") String token) {

        Integer uid = checkSession(token);

        if (uid != null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        DefaultOAuth1Provider.Token tok1 = App.oap.getRequestToken(oauth_token);

        if (tok1 == null) {

            return Response.status(Response.Status.NOT_FOUND).build();

        } else {

            String answ = App.oap.authorizeToken(tok1, null, null);

            return Response.ok(answ, MediaType.TEXT_HTML).build();
        }

    }

}
