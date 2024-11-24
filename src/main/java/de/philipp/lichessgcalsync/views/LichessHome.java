package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Lichess Sync")
@PermitAll
public class LichessHome extends VerticalLayout {
	public LichessHome() {
		Button googleButton = new Button("Connect Google Calendar");
		
		googleButton.addClickListener(e -> {
			UI.getCurrent().navigate(CallbackView2.class);
		});
	}
}
