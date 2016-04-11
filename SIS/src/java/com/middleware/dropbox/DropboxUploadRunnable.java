package com.middleware.dropbox;

import com.dropbox.core.DbxException;
import com.middleware.dropbox.DropboxClient;
import com.middleware.rest.priv.ImageREST;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DropboxUploadRunnable implements Runnable {

    private String dropboxToken, uploadName, path;

    public DropboxUploadRunnable(String dropboxToken, String path, String uploadName) {
        this.dropboxToken = dropboxToken;
        this.uploadName = uploadName;
        this.path = path;
    }

    @Override
    public void run() {
        
        java.nio.file.Path destination = Paths.get(path);
   
        
        try {
            DropboxClient.upload(dropboxToken, destination.toFile(), uploadName);
        } catch (DbxException ex) {
            Logger.getLogger(ImageREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        destination.toFile().delete();
    }

}