package de.philipp.lichessgcalsync.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar.readonly3");
    private static final String CREDENTIALS_FILE_PATH = "classpath:credentials.json";
    
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    
    public record GoogleCallback(Credential credential, GoogleAuthorizationCodeRequestUrl redirectUri) {};
    
    private GoogleAuthorizationCodeFlow flow;
    
    
	public GoogleCalendarService() {
	}

    public GoogleAuthorizationCodeFlow initFlow() throws IOException, GeneralSecurityException {
    	System.out.println("initFlow2");
    	ResourceLoader resourceLoader = new DefaultResourceLoader();
    	Resource resource = resourceLoader.getResource(CREDENTIALS_FILE_PATH);
    	InputStream in = resource.getInputStream();
    	
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        this.flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        
        return flow;
    }
    
    public String getAuthURI() {
    	return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    public List<Event> getUpcomingEvents(Credential credentials) throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
    
	public void getCalendars(Credential credentials) throws IOException, GeneralSecurityException {
		
	}
        		                
    
    public Credential getCredentialsFromCode(String code) throws Exception {
        TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        return flow.createAndStoreCredential(tokenResponse, "user");
    }
    
	public void logout() throws IOException {
		File tokenDirectory = new File(TOKENS_DIRECTORY_PATH);
		if (tokenDirectory.exists()) {
			for (File file : tokenDirectory.listFiles()) {
				if (!file.delete()) {
					throw new IOException("Failed to delete token file: " + file.getAbsolutePath());
				}
			}
		}
	}
	
	public GoogleAuthorizationCodeFlow getFlow() {
		return this.flow;
	}
}