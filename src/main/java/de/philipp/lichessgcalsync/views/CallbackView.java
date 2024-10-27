package de.philipp.lichessgcalsync.views;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;

import de.philipp.lichessgcalsync.service.LichessService;
import jakarta.annotation.security.PermitAll;

@Route("Callback")
@PageTitle("Lichess Callback")
@PermitAll

public class CallbackView extends VerticalLayout {

	private static final String LOGOUT_SUCCESS_URL = "/";
	private VerticalLayout mainLayout = new VerticalLayout();
	private LichessService lichessService;

	public CallbackView(LichessService lichessService) {
		this.lichessService = lichessService;
		mainLayout.setAlignItems(Alignment.CENTER);

		constructGoogleLoginElements();
		getLastLichessGames();
		add(mainLayout);
	}

	private void constructGoogleLoginElements() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
		String givenName = principal.getAttribute("given_name");
		String familyName = principal.getAttribute("family_name");
		String email = principal.getAttribute("email");
		String picture = principal.getAttribute("picture");

		H2 header = new H2("Hello " + givenName + " " + familyName + " (" + email + ")");
		Image image = new Image(picture, "User Image");

		Button logoutButton = new Button("Logout", click -> {
			UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
			logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
		});

		mainLayout.add(header, image, logoutButton);
	}
	
	private void getLastLichessGames() {
		String userGames = lichessService.getUserGames("Philippusk", 3);
		System.out.println(userGames);
	}
}
