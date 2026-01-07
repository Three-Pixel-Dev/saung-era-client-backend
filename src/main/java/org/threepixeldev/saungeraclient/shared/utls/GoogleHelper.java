package org.threepixeldev.saungeraclient.shared.utls;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleHelper {

    @Value("${google.webclient.id}")
    private String webClientId;
     @Value("${google.android.client.id}")
     private String androidClientId;

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            List<String> trustedClientIds = Arrays.asList(
                    webClientId,
                    androidClientId
            );
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory())
                    .setAudience(trustedClientIds)
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                return idToken.getPayload();
            }
            throw new IllegalArgumentException("Invalid Google ID Token");
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Token verification failed: " + e.getMessage());
        }
    }
}