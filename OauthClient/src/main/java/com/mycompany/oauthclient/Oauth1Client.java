package com.mycompany.oauthclient;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

public class Oauth1Client {

    static final String BASE_URL = "http://52.38.31.117:8080/SIS/rest";
    static final String oauth_key = "n8jm0kberefc7o5p54m9ubkecm";
    static final String ouath_signature = "1u3j9a4odtpcsnriosqdck6pdh";

    public static void main(String[] args) throws IOException {

        ConsumerCredentials consumerCredentials = new ConsumerCredentials(oauth_key, ouath_signature);
        //TODO - user proper client builder with real location + any ssl context
        OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
                .authorizationFlow(
                        BASE_URL + "/oauth1/request_token",
                        BASE_URL + "/oauth1/access_token",
                        BASE_URL.replace("rest", "") + "angularmark/oauth1/authorize")
                .build();
        String authorizationUri = authFlow.start();
        authorizationUri = authorizationUri.replace("angularmark", "#");
        
        // Open authorization page or print out the uri
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(authorizationUri));
            } catch (IOException ex) {
               
            } catch (URISyntaxException ex) {
               
            }
        } else {
               System.out.println("Auth URI: " + authorizationUri);
        }

        System.out.print("\n\nVerifier: ");
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String verifier = bufferRead.readLine();

        final AccessToken accessToken = authFlow.finish(verifier);
        System.out.println("\nToken: " + accessToken.getToken() + "\nToken Secret: "+ accessToken.getAccessTokenSecret());

    }

}
