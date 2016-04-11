package com.middleware.jms.components;

import com.middleware.jms.common.ImageInfo;
import com.middleware.api.API;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javaxt.io.Image;
import com.middleware.amazon.S3Client;
import java.io.FileOutputStream;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@MessageDriven(mappedName = "toCropResize")
public class CropResize implements MessageListener {

    @Inject
    JMSContext jmsContext;
    @Resource(lookup = "toGreyScale")
    Queue toGreyScale;
    @Resource(lookup = "toCollage")
    Queue toCollage;
    
    
    private static EntityManagerFactory factory;
    private static EntityManager em;

    public CropResize() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @Override
    public void onMessage(Message msg) {

        try {
            ImageInfo imageInfo = msg.getBody(ImageInfo.class);
            System.out.println("CropResize received images:\n  " + imageInfo);

            // Do resizing job
            Image[] images = new Image[4];
            for (int i = 0; i < imageInfo.getImages().length; i++) {
                images[i] = new Image(S3Client.download(imageInfo.getImages()[i]));
                S3Client.delete(imageInfo.getImages()[i]);
                (new File(imageInfo.getImages()[i])).delete();
            }

            if (imageInfo.getOption().equals(ImageInfo.Option.CROP)) {
                cropImages(images);
            } else if (imageInfo.getOption().equals(ImageInfo.Option.RESIZE)) {
                resizeImages(images);
            }

            String[] output = new String[4];

            for (int i = 0; i < output.length; i++) {
                output[i] = imageInfo.getUploadSessionID() + "-tempcropresize-" + imageInfo.getImages()[i];

                byte[] byteArray = images[i].getByteArray();
                FileOutputStream fos = new FileOutputStream(output[i]);
                fos.write(byteArray);
                fos.close();

                S3Client.upload(output[i], output[i]);
                (new File(output[i])).delete();
            }

            imageInfo.setImages(output);
            ObjectMessage objMessage = jmsContext.createObjectMessage();
            objMessage.setObject(imageInfo);

            // Update DB status
            API.updateStatus(em, imageInfo.getUploadSessionID().toString(), "2");

            // To the next step
            JMSProducer producer = jmsContext.createProducer();

            if (imageInfo.isApplyGreyScale()) {
                producer.send(toGreyScale, objMessage);
            } else {
                producer.send(toCollage, objMessage);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(CropResize.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Image Tools
    private static void cropImages(Image[] images) {

        Image smallest = getSmallestImage(images);

        //Set new dimensions equals to the smallest dimension
        int newWidth = smallest.getWidth();
        int newHeight = smallest.getHeight();
        if (newWidth < newHeight) {
            newHeight = newWidth;
        } else {
            newWidth = newHeight;
        }
        //Crop images
        for (Image img : images) {
            img.crop((img.getWidth() - newWidth) / 2, (img.getHeight() - newHeight) / 2, newWidth, newHeight);
        }
    }

    private static void resizeImages(Image[] images) {

        Image smallest = getSmallestImage(images);

        //Set new dimensions equals to the smallest dimension
        int newWidth = smallest.getWidth();
        int newHeight = smallest.getHeight();
        if (newWidth < newHeight) {
            newHeight = newWidth;
        } else {
            newWidth = newHeight;
        }
        //Resize images
        for (Image img : images) {
            img.resize(newWidth, newHeight);
        }
    }

    private static Image getSmallestImage(Image[] images) {
        int currentArea, minArea = images[0].getWidth() * images[0].getHeight();
        Image smallest = images[0];

        for (Image img : images) {
            currentArea = img.getWidth() * img.getHeight();
            if (currentArea < minArea) {
                minArea = currentArea;
                smallest = img;
            }
        }
        return smallest;
    }
}
