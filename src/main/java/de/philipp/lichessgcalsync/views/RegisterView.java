package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.philipp.lichessgcalsync.service.LichessUser;
import de.philipp.lichessgcalsync.service.LichessUserDetailService;
import de.philipp.lichessgcalsync.views.RegisterForm.SaveEvent;

@Route(value = "register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
	
	private LichessUserDetailService userService;
	private RegisterForm form;

	public RegisterView(LichessUserDetailService userService) {
		this.userService = userService;
		constructUI();
    }
	
		private void constructUI() {
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		
		configureForm();
		
		VerticalLayout formLayout = new VerticalLayout();
		formLayout.setWidth("300px");
		formLayout.add(form);
		add(formLayout);
	}
		
		private void configureForm() {
		form = new RegisterForm();
		form.addSaveListener(this::registerUser);
	}

	private void registerUser(SaveEvent event) {
		LichessUser user = event.getUser();
		boolean saveUser = userService.saveUser(user);
	}
}
