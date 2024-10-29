package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.philipp.lichessgcalsync.service.LichessService;

@Route("Callback2")
@PageTitle("Lichess Callback")
@AnonymousAllowed

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

//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
//		String givenName = principal.getAttribute("given_name");
//		String familyName = principal.getAttribute("family_name");
//		String email = principal.getAttribute("email");
//		String picture = principal.getAttribute("picture");
//
//		H2 header = new H2("Hello " + givenName + " " + familyName + " (" + email + ")");
//		Image image = new Image(picture, "User Image");
//
//		Button logoutButton = new Button("Logout", click -> {
//			UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
//			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
//			logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
//		});

		mainLayout.add();
	}
	
	private void getLastLichessGames() {
		String userGames = lichessService.getUserGames("Philippusk", 3);
		System.out.println(userGames);
	}
}
