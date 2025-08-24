package com.trygve_backend.trygve.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FireBaseConfig {

    @Value("${firebase.service-account.path}")
    private Resource serviceAccount;

    @Bean
    public FirebaseApp fireBaseApp() throws IOException{
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build();

        if(FirebaseApp.getApps().isEmpty())
        {
            return FirebaseApp.initializeApp(options);
        }
        else {
            return FirebaseApp.getInstance();
        }
    }
}
