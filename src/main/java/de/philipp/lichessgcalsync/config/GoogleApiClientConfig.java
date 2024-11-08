package de.philipp.lichessgcalsync.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;



/**
 * Auto-configuration for {@link GoogleCredential}, {@link NetHttpTransport}, and {@link
 * JacksonFactory}.
 */
@Configuration
public class GoogleApiClientConfig {

	public GoogleApiClientConfig() {
	}

    /**
     * Provides a unmodifiable {@link Set<String>} of Google OAuth 2.0 scopes to be used.
     *
     * @return An unmodifiable {@link Set<String>}
     */
    private Set<String> googleOAuth2Scopes() {
        Set<String> googleOAuth2Scopes = new HashSet<>();
        googleOAuth2Scopes.add(CalendarScopes.CALENDAR_READONLY);
        return Collections.unmodifiableSet(googleOAuth2Scopes);
    }

    /**
     * A preconfigured HTTP client for calling out to Google APIs.
     *
     * @return {@link NetHttpTransport}
     */
    @Bean
    public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    /**
     * Abstract low-level JSON factory.
     *
     * @return {@link JacksonFactory}
     */
    @Bean
	public JsonFactory jacksonFactory() {
		return GsonFactory.getDefaultInstance();
	}
}
