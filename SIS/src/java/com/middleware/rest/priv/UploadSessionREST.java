package com.middleware.rest.priv;

import com.middleware.amazon.S3Client;
import com.middleware.jms.common.ImageInfo;
import com.middleware.model.Uploadsession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/private/editor")
public class UploadSessionREST {

    @Context
    UriInfo uriInfo;

    private EntityManagerFactory factory;
    private EntityManager em;

    public UploadSessionREST() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uploadsession createUploadSession() {

        Uploadsession us = new Uploadsession();
        us.setStatus(0);
        us.setUploaded(0);
        em.getTransaction().begin();
        em.persist(us);
        em.getTransaction().commit();

        return us;
    }

    @POST
    @Path("{id}/upload/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postNewImage(
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            @PathParam("id") String sessionID) throws FileNotFoundException {

        Integer uploadSessionID = Integer.parseInt(sessionID);

        Uploadsession us = null;

        em.getTransaction().begin();
        us = em.find(Uploadsession.class, uploadSessionID);

        if (us.getUploaded() == 4) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        int newUploaded = us.getUploaded() + 1;
        String fileName = us.getId() + "-" + newUploaded + "-" + fileDisposition.getFileName();

        // store locally
        final java.nio.file.Path destination = Paths.get(fileName);

        try {
            Files.copy(fileStream, destination);
        } catch (IOException ex) {
            Logger.getLogger(UploadSessionREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (us.getUploaded()) {
            case 0:
                us.setImg1(fileName);
                break;
            case 1:
                us.setImg2(fileName);
                break;
            case 2:
                us.setImg3(fileName);
                break;
            case 3: {
                us.setImg4(fileName);
            }
        }

        us.setUploaded(newUploaded);
        em.merge(us);
        em.getTransaction().commit();

        return Response.status(Response.Status.OK).build();

    }

    @POST
    @Path("{id}/options/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setOptions(@PathParam("id") String id, MultivaluedMap<String, String> params) {

        Uploadsession us = null;
        Integer usid = Integer.parseInt(id);

        em.getTransaction().begin();
        us = em.find(Uploadsession.class, usid);
        if (params.getFirst("crop").equals("true")) {
            us.setIsCrop(true);
        } else {
            us.setIsCrop(false);
        }
        if (params.getFirst("greyscale").equals("true")) {
            us.setIsGreyScale(true);
        } else {
            us.setIsGreyScale(false);
        }

        if (us.getUploaded() == 4) {
            us.setStatus(1);
        }
        em.merge(us);
        em.getTransaction().commit();

        startEditing(us);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uploadsession getStatus(@PathParam("id") String id) {

        Uploadsession us = null;
        Integer usid = Integer.parseInt(id);

        em.getTransaction().begin();
        us = em.find(Uploadsession.class, usid);
        em.refresh(us);
        em.getTransaction().commit();

        em.getTransaction().begin();
        us = em.find(Uploadsession.class, usid);
        em.getTransaction().commit();

        return us;
    }

    // Image Editing
    private void startEditing(Uploadsession us) {

        try {
            // Upload to amazon AWS
            S3Client.upload(us.getImg1(), us.getImg1());
            S3Client.upload(us.getImg2(), us.getImg2());
            S3Client.upload(us.getImg3(), us.getImg3());
            S3Client.upload(us.getImg4(), us.getImg4());

            // delete from local sotrage
            (new File(us.getImg1())).delete();
            (new File(us.getImg2())).delete();
            (new File(us.getImg3())).delete();
            (new File(us.getImg4())).delete();

            // Get the contenxt
            javax.naming.Context initialContext = UploadSessionREST.getContext();

            // Get Destination
            Queue next = (Queue) initialContext.lookup("toCropResize");

            // Get JMSContext
            JMSContext jmsContext = ((ConnectionFactory) initialContext.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();

            // Create Producer
            JMSProducer producer = jmsContext.createProducer();

            ImageInfo message = new ImageInfo();
            String[] images = new String[4];
            images[0] = us.getImg1();
            images[1] = us.getImg2();
            images[2] = us.getImg3();
            images[3] = us.getImg4();
            message.setImages(images);

            message.setUploadSessionID(us.getId());

            if (us.getIsCrop()) {
                message.setOption(ImageInfo.Option.CROP);
            } else {
                message.setOption(ImageInfo.Option.RESIZE);
            }

            message.setApplyGreyScale(us.getIsGreyScale());

            ObjectMessage objMessage = jmsContext.createObjectMessage();
            objMessage.setObject(message);

            producer.send(next, objMessage);

        } catch (NamingException ex) {
            Logger.getLogger(UploadSessionREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(UploadSessionREST.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static javax.naming.Context getContext() throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
        return new InitialContext(props);
    }

}
