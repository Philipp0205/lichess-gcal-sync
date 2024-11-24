package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
	private LoginForm loginForm = new LoginForm();

	public LoginView() {
		setAlignItems(Alignment.CENTER);
		LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");
        Button registerButton = new Button("Register", e -> UI.getCurrent().navigate(RegisterView.class));

		add(loginForm, registerButton);
	}
	
	@Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
            .getQueryParameters()
            .getParameters()
            .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}