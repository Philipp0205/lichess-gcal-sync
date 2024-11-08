package de.philipp.lichessgcalsync.views;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.philipp.lichessgcalsync.config.GoogleAuthorizationService;
import de.philipp.lichessgcalsync.config.GoogleCalendarConfig;
import de.philipp.lichessgcalsync.service.GoogleCalendarService2;
import de.philipp.lichessgcalsync.service.LichessService;

@Route("callback")
@PageTitle("Lichess Callback")
@AnonymousAllowed
public class CallbackView2 extends VerticalLayout implements BeforeEnterObserver {

	private static final String LOGOUT_SUCCESS_URL = "/";
	private VerticalLayout mainLayout = new VerticalLayout();
	private LichessService lichessService;
	
	private GoogleCalendarService2 googleCalendarService;
	private GoogleAuthorizationService googleAuthorizationService;
	private Credential credentials;
	GoogleCalendarConfig googleCalendarConfig;

	public CallbackView2(GoogleCalendarService2 googleCalendarService,
			GoogleAuthorizationService googleAuthorizationService, GoogleCalendarConfig googleCalendarConfig) {
		this.googleAuthorizationService = googleAuthorizationService;
		this.googleCalendarService = googleCalendarService;
		this.googleCalendarConfig = googleCalendarConfig;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
	    QueryParameters queryParameters = event.getLocation().getQueryParameters();
	    Map<String, List<String>> parametersMap = queryParameters.getParameters();

	    List<String> codeList = parametersMap.get("code");
	    if (codeList != null && !codeList.isEmpty()) {
	        String code = codeList.get(0);
	        System.out.println("Code found: " + code);

	        VaadinSession.getCurrent().setAttribute("code", code);

	        try {
	            Credential credentialsFromCode = googleAuthorizationService.createCredentialsFromCode(code); // Exchange code for tokens
	            googleAuthorizationService.googleCredentials();
	            googleCalendarConfig.googleCalendar();
	            getEvents();
	        } catch (Exception e) {
	            System.out.println("Authorization failed.");
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No authorization code found.");
	    }
	}
	
	private void getEvents() {
		try {
			com.google.api.services.calendar.model.CalendarList execute = googleCalendarService.getCalendars().list().execute();
			execute.values().forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
