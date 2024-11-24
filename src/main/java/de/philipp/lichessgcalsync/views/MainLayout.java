package de.philipp.lichessgcalsync.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    private final transient AuthenticationContext authContext;

	public MainLayout(AuthenticationContext authContext) {
		this.authContext = authContext;
		H1 title = new H1("Lichess Sync");
		HorizontalLayout header = new HorizontalLayout(title);
		
		DrawerToggle toggle = new DrawerToggle();

		title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

		SideNav nav = new SideNav();
		

		Scroller scroller = new Scroller(nav);
		scroller.setClassName(LumoUtility.Padding.SMALL);

		addToDrawer(scroller);
		addToNavbar(header, toggle, title);
	}
}
