package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.philipp.lichessgcalsync.config.GoogleAuthorizationService;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private GoogleAuthorizationService authService; 
    private String authUrl;

	public LoginView(GoogleAuthorizationService authService, String authUrl ) {
		this.authUrl = authUrl;
		this.authService = authService;
		TextField lichessUsername = new TextField();

//		oauthUrl = flow.newAuthorizationUrl().setRedirectUri("http://localhost:8080/callback").build();
		System.out.println("OAuth URL: " + authUrl);

		Button loginButton = new Button("Login with Google", event -> UI.getCurrent().getPage().setLocation(authUrl));
		add(lichessUsername, loginButton);
	}
}