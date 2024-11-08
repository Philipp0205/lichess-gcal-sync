package de.philipp.lichessgcalsync.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.Calendar;

/** Auto-configuration for {@link Calendar}. */
@Configuration
public class GoogleCalendarConfig {
    private final GoogleAuthorizationService googleAuthorizationService;
    private NetHttpTransport netHttpTransport;
    private JsonFactory jacksonFactory;

	public GoogleCalendarConfig(GoogleAuthorizationService googleAuthService, NetHttpTransport netHttpTransport,
			JsonFactory jacksonFactory) {
		this.googleAuthorizationService = googleAuthService;
		this.netHttpTransport = netHttpTransport;
		this.jacksonFactory = jacksonFactory;
	}

    /**
     * Provides a Google Calendar client.
     *
     * @return {@link Calendar}
     * @throws GeneralSecurityException 
     * @throws IOException 
     */
    @Lazy
    @Bean
	public Calendar googleCalendar() {
    	System.out.println("googleCalendar bean called");
    	Credential googleCredentials = googleAuthorizationService.googleCredentials();
    	Long expiresInSeconds = googleCredentials.getExpiresInSeconds();
    	System.out.println("googleCredentials expiresInSeconds: " + expiresInSeconds);
		return new Calendar(netHttpTransport, jacksonFactory, googleCredentials);
	}
}