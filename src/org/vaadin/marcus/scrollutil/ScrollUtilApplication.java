package org.vaadin.marcus.scrollutil;

import org.vaadin.marcus.scrollutil.ScrollUtil.ProximityEvent;
import org.vaadin.marcus.scrollutil.ScrollUtil.ProximityEventListener;

import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ScrollUtilApplication extends Application {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
        Window mainWindow = new Window("Scroller Demo Application");
        setMainWindow(mainWindow);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("5000px");

        final ScrollUtil scroller = new ScrollUtil();
        scroller.addListener(new ProximityEventListener() {

            private static final long serialVersionUID = 1L;

            public void proximityDetected(ProximityEvent event) {
                getMainWindow().showNotification("Proximity event!!");
            }
        });

        Button initiateScrollButton = new Button("Scroll down");
        final Button scrollToButton = new Button("Here I am!");

        layout.addComponent(initiateScrollButton);
        layout.addComponent(scroller);
        layout.addComponent(scrollToButton);
        layout.setExpandRatio(initiateScrollButton, 1);
        layout.setExpandRatio(scrollToButton, 1);
        layout.setComponentAlignment(initiateScrollButton, Alignment.TOP_CENTER);
        layout.setComponentAlignment(scrollToButton, Alignment.MIDDLE_CENTER);

        mainWindow.setContent(layout);

        initiateScrollButton.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                scroller.scrollTo(scrollToButton);
            }
        });
        setTheme("scrollutiltheme");

    }

}
