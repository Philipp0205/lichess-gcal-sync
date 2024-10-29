package de.philipp.lichessgcalsync.views;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.model.Event;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.philipp.lichessgcalsync.service.GoogleCalendarService;
import de.philipp.lichessgcalsync.service.LichessService;

@Route("callback")
@PageTitle("Lichess Callback")
@AnonymousAllowed
public class CallbackView2 extends VerticalLayout implements BeforeEnterObserver {

	private static final String LOGOUT_SUCCESS_URL = "/";
	private VerticalLayout mainLayout = new VerticalLayout();
	private LichessService lichessService;
	
	private GoogleCalendarService googleCalendarService;
	private Credential credentials;

	public CallbackView2(GoogleCalendarService googleCalendarService) {
		this.googleCalendarService = googleCalendarService;
	}

	@Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Extract query parameters from the URL
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        // Get the 'code' parameter from the map
        List<String> codeList = parametersMap.get("code");
        if (codeList != null && !codeList.isEmpty()) {
        	System.out.println("code found: " + codeList.get(0));
            String code = codeList.get(0); // Get the first value of 'code'
            try {
                credentials = googleCalendarService.getCredentialsFromCode(code); // Exchange code for tokens
                List<Event> events = getEvents();
                Text eventText = new Text(events.toString());
                add(eventText);
            } catch (Exception e) {
            	System.out.println("Authorization failed.");
                e.printStackTrace();
            }
        } else {
        	System.out.println("No authorization code found.");
        }
    }
	
	private List<Event> getEvents() {
		try {
			return googleCalendarService.getUpcomingEvents(credentials);
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
