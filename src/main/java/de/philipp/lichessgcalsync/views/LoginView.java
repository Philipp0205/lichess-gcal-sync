package de.philipp.lichessgcalsync.views;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.philipp.lichessgcalsync.service.GoogleCalendarService;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private String oauthUrl;

	public LoginView(GoogleCalendarService googleCalendarService) {
		TextField lichessUsername = new TextField();

		try {
			googleCalendarService.initFlow();
			oauthUrl = googleCalendarService.getAuthURI();
			System.out.println("OAuth URL: " + oauthUrl);
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
			// You might want to add error handling here
		}

		Button loginButton = new Button("Login with Google", event -> UI.getCurrent().getPage().setLocation(oauthUrl));
		add(lichessUsername, loginButton);
	}
}
