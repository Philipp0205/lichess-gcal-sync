package de.philipp.lichessgcalsync.views.opml;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import de.philipp.lichessgcalsync.views.MainLayout;

@Route(value = "opml", layout = MainLayout.class)
public class OpmlView extends VerticalLayout {

	public OpmlView() {
		TextArea textArea = new TextArea();
		textArea.setWidthFull();
		textArea.setLabel("Urls");
		add(textArea);
	}
}
