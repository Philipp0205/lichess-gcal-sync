package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;

import de.philipp.lichessgcalsync.service.LichessUser;

public class RegisterForm extends FormLayout {
	private Binder<LichessUser> binder = new Binder<>(LichessUser.class);
	
	private H1 title = new H1("Register");
	private TextField email = new TextField("Email");
	private PasswordField password = new PasswordField("Password");
	private PasswordField confirmPassword = new PasswordField("Confirm Password");
	private Button save = new Button("Save");
	private Button cancel = new Button("Cancel");
	
	    public RegisterForm() {
	    	binder.bindInstanceFields(this);
	    	
	    	binder.forField(email)
	    	.asRequired("Email is required")
            .withValidator(new EmailValidator("Please enter a valid email address"))
            .bind(LichessUser::getEmail, LichessUser::setEmail);
	    	
			binder.forField(password)
			.asRequired("Password is required")
			.withValidator(new StringLengthValidator("Password must be at least 5 characters", 5, null))
			.bind(LichessUser::getPassword, LichessUser::setPassword);
	    	
	    	
	    	add(title, email, password, confirmPassword, createButtonsLayout());
	    }
	    
		private FormLayout createButtonsLayout() {
			save.addClickListener(event -> validateAndSave());
			cancel.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));

			email.setRequired(true);
			password.setRequired(true);
			confirmPassword.setRequired(true);

			return new FormLayout(save, cancel);
		}

		private void validateAndSave() {
			System.out.println("validateAndSave");
			if (!password.getValue().equals(confirmPassword.getValue())) {
				confirmPassword.setInvalid(true);
				password.setErrorMessage("Passwords do not match");
			}

			LichessUser user = new LichessUser(); // Assuming you have a default constructor
			user.setEmail(email.getValue());
			user.setPassword(password.getValue());
			binder.setBean(user); // Bind the user to the form's fields
			
			System.out.println("email: " + email.getValue());

			if (binder.validate().isOk()) {
				fireEvent(new SaveEvent(this, binder.getBean())); // Pass the updated user
			} else {
				Notification.show("Please check your input", 3000, Position.MIDDLE);
			}
		}
		
		public void setUser(LichessUser user) {
			binder.setBean(user);
		}
		
		// Custom events for save and close actions
		public static abstract class RegisterFormEvent extends ComponentEvent<RegisterForm> {
			private LichessUser user;

			protected RegisterFormEvent(RegisterForm source, LichessUser user) {
				super(source, false);
				this.user = user;
			}

			public LichessUser getUser() {
				return user;
			}
		}
		
		public static class SaveEvent extends RegisterFormEvent {
			SaveEvent(RegisterForm source, LichessUser user) {
				super(source, user);
			}
		}
		
		public static class CloseEvent extends RegisterFormEvent {
			CloseEvent(RegisterForm source) {
				super(source, null);
			}
		}
		
		public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
			return addListener(SaveEvent.class, listener);
		}
		
		public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
			return addListener(CloseEvent.class, listener);
		}
}