package com.middleware.jms.components;

import com.middleware.jms.common.ImageInfo;
import com.middleware.api.API;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import com.middleware.amazon.S3Client;
import java.util.logging.Logger;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javaxt.io.Image;
import java.io.FileOutputStream;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@MessageDriven(mappedName = "toGreyScale")
public class GreyScale implements MessageListener {

    @Inject
    JMSContext jmsContext;
    @Resource(lookup = "toCollage")
    Queue toCollage;
    
    private static EntityManagerFactory factory;
    private static EntityManager em;

    public GreyScale() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @Override
    public void onMessage(Message msg) {

        try {
            ImageInfo imageInfo = msg.getBody(ImageInfo.class);
            System.out.println("GreyScale received image\n  " + imageInfo);

            // Do greyscale job
            Image[] images = new Image[4];
            for (int i = 0; i < imageInfo.getImages().length; i++) {
                images[i] = new Image(S3Client.download(imageInfo.getImages()[i]));
                S3Client.delete(imageInfo.getImages()[i]);
                (new File(imageInfo.getImages()[i])).delete();
            }
            
            greyScaleConversion(images);

            // update the message
            String[] output = new String[4];
            for (int i = 0; i < imageInfo.getImages().length; i++) {
                output[i] = imageInfo.getUploadSessionID() + "-tempgreyscale-" + imageInfo.getImages()[i];

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
            API.updateStatus(em, imageInfo.getUploadSessionID().toString(), "3");

            // To the next step
            JMSProducer producer = jmsContext.createProducer();
            producer.send(toCollage, objMessage);

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(GreyScale.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Image Editing
    private static void greyScaleConversion(Image[] images) {
        for (Image img : images) {
            BufferedImage image = img.getBufferedImage();

            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color c = new Color(image.getRGB(j, i));
                    int red = (int) (c.getRed() * 0.299);
                    int green = (int) (c.getGreen() * 0.587);
                    int blue = (int) (c.getBlue() * 0.114);
                    Color newColor = new Color(red + green + blue,
                            red + green + blue, red + green + blue);

                    image.setRGB(j, i, newColor.getRGB());
                }
            }
        }
    }
}
