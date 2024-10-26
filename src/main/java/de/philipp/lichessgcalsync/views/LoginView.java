package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    // URL that Spring Security uses to connect to Google services
    private static final String GOOGLE_OAUTH_URL = "/oauth2/authorization/google";
    
    public LoginView() {
        // Create buttons for Google and Lichess logins
        Button googleLoginButton = new Button("Login with Google");
        Button lichessLoginButton = new Button("Login with Lichess");

        // Add click listeners to navigate to the respective OAuth URLs
        googleLoginButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.getPage().setLocation(GOOGLE_OAUTH_URL));
        });

        add(googleLoginButton, lichessLoginButton);
    }
	
	@Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		// TODO
    }
}