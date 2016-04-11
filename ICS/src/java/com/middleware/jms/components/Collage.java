package com.middleware.jms.components;

import com.middleware.jms.common.ImageInfo;
import com.middleware.api.API;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javaxt.io.Image;
import com.middleware.amazon.S3Client;
import java.io.FileOutputStream;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@MessageDriven(mappedName = "toCollage")
public class Collage implements MessageListener {

    @Inject
    JMSContext jmsContext;

    private static EntityManagerFactory factory;
    private static EntityManager em;

    public Collage() {
        factory = Persistence.createEntityManagerFactory("RESTPU");
        em = factory.createEntityManager();
    }

    @Override
    public void onMessage(Message msg) {

        try {
            ImageInfo imageInfo = msg.getBody(ImageInfo.class);
            System.out.println("Collage received images\n  " + imageInfo);

            // Do collage job
            Image[] images = new Image[4];
            for (int i = 0; i < imageInfo.getImages().length; i++) {
                images[i] = new Image(S3Client.download(imageInfo.getImages()[i]));
                S3Client.delete(imageInfo.getImages()[i]);
                (new File(imageInfo.getImages()[i])).delete();
            }

            Image result = collage(images);

            String resultName = imageInfo.getUploadSessionID() + "-result.jpg";

            byte[] byteArray = result.getByteArray();
            FileOutputStream fos = new FileOutputStream(resultName);
            fos.write(byteArray);
            fos.close();

            String URL = S3Client.uploadPublic(resultName, resultName);

            (new File(resultName)).delete();

            // Update DB status
            API.setFinalURL(em,imageInfo.getUploadSessionID().toString(), URL);
            API.updateStatus(em,imageInfo.getUploadSessionID().toString(), "4");

            System.out.println(URL);

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(Collage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Image Editing
    private Image collage(Image[] images) {

        Image smallest = getSmallestImage(images);

        int centerX = smallest.getWidth();
        int centerY = smallest.getHeight();

        Image finalImg = new Image(2 * centerX, 2 * centerY);

        for (int sourceX = 0; sourceX < images[0].getWidth(); sourceX++) {
            for (int sourceY = 0; sourceY < images[0].getHeight(); sourceY++) {
                //Copy pixel-by-pixel starting from point(0, 0) to point (centerX, centerY)
                int rgb = images[0].getBufferedImage().getRGB(sourceX, sourceY);
                finalImg.getBufferedImage().setRGB(sourceX, sourceY, rgb);
            }
        }

        for (int sourceX = 0; sourceX < images[1].getWidth(); sourceX++) {
            for (int sourceY = 0; sourceY < images[1].getHeight(); sourceY++) {
                //Copy pixel-by-pixel starting from point(centerX, 0) to point (2*centerX, centerY)
                int rgb = images[1].getBufferedImage().getRGB(sourceX, sourceY);
                finalImg.getBufferedImage().setRGB(sourceX + centerX, sourceY, rgb);
            }
        }

        for (int sourceX = 0; sourceX < images[2].getWidth(); sourceX++) {
            for (int sourceY = 0; sourceY < images[2].getHeight(); sourceY++) {
                //Copy pixel-by-pixel starting from point(0, centerY) to point (centerX, 2*centerY)
                int rgb = images[2].getBufferedImage().getRGB(sourceX, sourceY);
                finalImg.getBufferedImage().setRGB(sourceX, sourceY + centerY, rgb);
            }
        }

        for (int sourceX = 0; sourceX < images[3].getWidth(); sourceX++) {
            for (int sourceY = 0; sourceY < images[3].getHeight(); sourceY++) {
                //Copy pixel-by-pixel starting from point(centerX, centerY) to point (2*centerX, 2*centerY)
                int rgb = images[3].getBufferedImage().getRGB(sourceX, sourceY);
                finalImg.getBufferedImage().setRGB(sourceX + centerX, sourceY + centerY, rgb);
            }
        }

        return finalImg;

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
