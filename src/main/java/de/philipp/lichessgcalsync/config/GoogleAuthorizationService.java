package de.philipp.lichessgcalsync.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;

@Service
public class GoogleAuthorizationService {
	private static final String CREDENTIALS_FILE_PATH = "classpath:credentials.json";
	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final String REDIRECT_URI = "http://localhost:8080/callback";

	private final NetHttpTransport netHttpTransport;
	private final JsonFactory jacksonFactory;
	private Credential googleCredentials;
	private GoogleAuthorizationCodeFlow flow;

	public GoogleAuthorizationService(NetHttpTransport netHttpTransport, JsonFactory jacksonFactory) {
		this.netHttpTransport = netHttpTransport;
		this.jacksonFactory = jacksonFactory;
		initiateOAuthFlow();
	}
	

	public Credential getCredential() throws IOException, GeneralSecurityException {
		if (googleCredentials == null || googleCredentials.getAccessToken() == null) {
			// Trigger OAuth flow here
			initiateOAuthFlow();
		}
		return googleCredentials;
	}

	private void initiateOAuthFlow() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(CREDENTIALS_FILE_PATH);
		InputStream in;
		try {
			in = resource.getInputStream();
			if (in == null) {
				throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
			}
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			this.flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(),
					JSON_FACTORY, clientSecrets, SCOPES)
					.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
					.setAccessType("offline").build();
			
		} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		// returns an authorized Credential object.
//		return credential;
	}

	public Credential createCredentialsFromCode(String code) throws Exception {
		System.out.println("create credentials from code");
		TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
		this.googleCredentials = flow.createAndStoreCredential(tokenResponse, "user");
		return this.googleCredentials;
	}

	@Lazy
	@Bean
	public Credential googleCredentials() {
		return this.googleCredentials;
	}

	@Bean
	public String authUrl() {
		if (flow == null) {
			throw new IllegalStateException("OAuth flow not initialized. Call initiateOAuthFlow() first.");
		}
		return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
	}
}
