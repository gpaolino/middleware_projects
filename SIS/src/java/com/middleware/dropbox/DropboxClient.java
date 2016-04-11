package com.middleware.dropbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxClientV2Base;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;
import static com.middleware.config.Config.DPBX_APP_KEY;
import static com.middleware.config.Config.DPBX_APP_SECRET;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DropboxClient {

    public static void upload(String token, File inputFile, String fileName) throws DbxException, IOException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2Base client = new DbxClientV2(config, token);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        try (InputStream in = new FileInputStream(inputFile)) {
            FileMetadata metadata = client.files().uploadBuilder("/"+fileName)
                .uploadAndFinish(in);
        }


        //return client.sharing().createSharedLink(path).getUrl().replace("dl=0", "raw=1");
    }

    // Starts the authorization process returning the authorization URL
    public static String getAuthorizationURL() {
        // Get your app key and secret from the Dropbox developers website.

        DbxAppInfo appInfo = new DbxAppInfo(DPBX_APP_KEY, DPBX_APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
                Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        return webAuth.start();
    }

    // Completes the authorization process taking as input the authorization obtained by the user 
    // authorizing the app. Returns the permanent token.
    public static String completeAuthorization(String authCode) {

        DbxAppInfo appInfo = new DbxAppInfo(DPBX_APP_KEY, DPBX_APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
                Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        DbxAuthFinish authFinish = null;
        try {
            authFinish = webAuth.finish(authCode);
        } catch (DbxException ex) {
            Logger.getLogger(DropboxClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return authFinish.getAccessToken();

    }
}
