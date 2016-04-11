package com.middleware.amazon;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import static com.middleware.config.Config.AMZ_ACCESS_KEY;
import static com.middleware.config.Config.AMZ_ACCESS_KEY_ID;
import static com.middleware.config.Config.AMZ_BUCKET;
import static com.middleware.config.Config.AMZ_BUCKET_ROOT;
import static com.middleware.config.Config.AMZ_REGION;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages downlod/upload operations from/to an Amazon S3 bucket
 */
public class S3Client {

    /**
     *
     * @param id identifier of the file in the S3 Bucket
     * @return the downloaded file path
     */
    public static String download(String id) {

        try {
            AWSCredentials credentials = new BasicAWSCredentials(AMZ_ACCESS_KEY_ID, AMZ_ACCESS_KEY);
            AmazonS3 s3Client = new AmazonS3Client(credentials);
            s3Client.setRegion(Region.getRegion(AMZ_REGION));

            S3Object object = s3Client.getObject(AMZ_BUCKET, id);

            String outName = id;

            InputStream in = object.getObjectContent();
            byte[] buf = new byte[1024];
            OutputStream out = new FileOutputStream(outName);

            int count = 0;
            while ((count = in.read(buf)) != -1) {
                out.write(buf, 0, count);
            }
            out.close();
            in.close();

            return outName;
        } catch (IOException ex) {
            Logger.getLogger(S3Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    /**
     *
     * @param id the id to be given to the uploaded file in the bucket
     * @param filePath path of the file to be uploaded
     */
    public static void upload(String id, String filePath) {

        AWSCredentials credentials = new BasicAWSCredentials(AMZ_ACCESS_KEY_ID, AMZ_ACCESS_KEY);
        AmazonS3 s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(AMZ_REGION));

        s3Client.putObject(AMZ_BUCKET, id, new File(filePath));

    }

    /**
     *
     * @param id identifier of the file in the S3 Bucket
     */
    public static void delete(String id) {

        AWSCredentials credentials = new BasicAWSCredentials(AMZ_ACCESS_KEY_ID, AMZ_ACCESS_KEY);
        AmazonS3 s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(AMZ_REGION));

        s3Client.deleteObject(AMZ_BUCKET, id);

    }

    /**
     *
     * @param id identifier of the file in the S3 Bucket
     * @param filePath path of the file to be uploaded
     * @return public URI of the uploaded file
     */
    public static String uploadPublic(String id, String filePath) {
        AWSCredentials credentials = new BasicAWSCredentials(AMZ_ACCESS_KEY_ID, AMZ_ACCESS_KEY);
        AmazonS3 s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(AMZ_REGION));

        PutObjectRequest putObj = new PutObjectRequest(AMZ_BUCKET, id, new File(filePath));

        //making the object Public
        putObj.setCannedAcl(CannedAccessControlList.PublicRead);

        s3Client.putObject(putObj);

        return AMZ_BUCKET_ROOT + id;
    }

}
