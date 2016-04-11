package com.middleware.rest.priv;

import com.middleware.amazon.S3Client;
import com.middleware.dropbox.DropboxUploadRunnable;
import com.middleware.model.Image;
import com.middleware.model.User;
import com.middleware.session.SessionTools;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/private/image")
public class ImageREST {

    @Context
    UriInfo uriInfo;

    private final EntityManagerFactory factory;
    private final EntityManager em;

    ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public ImageREST() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @GET
    @Path("{id}")
    public Response getImage(@PathParam("id") String id) {

        em.getTransaction().begin();
        Image image = em.find(Image.class, Integer.parseInt(id));
        em.getTransaction().commit();

        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response
                    .status(Response.Status.OK)
                    .entity(image)
                    .build();

        }

    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postNewImage(
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            @FormDataParam("title") String title,
            @HeaderParam("access-token") String token) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Integer uid = SessionTools.checkSession(token);

        if (uid == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Get the user
        em.getTransaction().begin();
        User user = em.find(User.class, uid);
        em.getTransaction().commit();

        String imageName = fileDisposition.getFileName();

        Image i = new Image();
        i.setFileName(imageName);
        i.setTitle(title);
        i.setUser(uid);
        i.setLocation("");

        em.getTransaction().begin();
        em.persist(i);
        em.getTransaction().commit();

        String location = "";
        String uploadName = i.getId() + "_" + imageName;

        // UPLOAD ON AMAZON S3
        // store locally
        java.nio.file.Path destination = Paths.get(uploadName);

        try {
            Files.copy(fileStream, destination);
        } catch (IOException ex) {
            Logger.getLogger(UploadSessionREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        location = S3Client.uploadPublic(uploadName, uploadName);

        // UPLOAD ON DROPBOX IF PAIRED
        if (user.getPairedWithDropbox()) {

            Runnable task = new DropboxUploadRunnable(user.getDropboxToken(), uploadName, uploadName);
            threadPool.execute(task);

        } else {
            destination.toFile().delete();
        }

        i.setLocation(location);

        em.getTransaction().begin();
        em.merge(i);
        em.getTransaction().commit();

        String uriString = uriInfo.getAbsolutePath().toString();
        uriString = uriString + i.getId();
        URI uri = URI.create(uriString);

        return Response
                .status(Response.Status.CREATED)
                .location(uri)
                .link(uri.toString(), "image").build();

    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id,
            @HeaderParam("access-token") String token
    ) {

        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Integer uid = SessionTools.checkSession(token);

        if (uid == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        em.getTransaction().begin();

        Image image = em.find(Image.class, Integer.parseInt(id));

        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (image.getUser() != uid) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Delete from S3
        S3Client.delete(image.getFileName());

        em.remove(image);

        em.getTransaction().commit();

        return Response.ok().build();
    }

}
