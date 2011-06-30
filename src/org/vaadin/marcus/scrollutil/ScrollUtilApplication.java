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
    private VerticalLayout layout;

    @Override
    public void init() {
        Window mainWindow = new Window("Scroller Demo Application");
        setMainWindow(mainWindow);

        layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("5000px");

        final ScrollUtil scroller = new ScrollUtil();
        scroller.addListener(new ProximityEventListener() {

            private static final long serialVersionUID = 1L;

            public void proximityDetected(ProximityEvent event) {
                getMainWindow().showNotification(
                        "Bottom component visibility event!!");
            }
        });

        Button initiateScrollButton = new Button("Scroll down");
        final Button scrollToButton = new Button("Here I am!");
        scrollToButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                addScrollDetectionToTop();
            }
        });

        layout.addComponent(initiateScrollButton);
        layout.addComponent(scrollToButton);
        layout.addComponent(scroller);
        layout.setExpandRatio(initiateScrollButton, 1);
        layout.setExpandRatio(scrollToButton, 1);
        layout.setComponentAlignment(initiateScrollButton, Alignment.TOP_CENTER);
        layout.setComponentAlignment(scrollToButton, Alignment.MIDDLE_CENTER);

        mainWindow.setContent(layout);

        initiateScrollButton.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {

            }
        });
        setTheme("scrollutiltheme");

    }

    protected void addScrollDetectionToTop() {
        ScrollUtil topListener = new ScrollUtil();
        topListener.addListener(new ProximityEventListener() {
            private static final long serialVersionUID = 1L;

            public void proximityDetected(ProximityEvent event) {
                getMainWindow().showNotification(
                        "Top component visibility event!!");
            }
        });

        layout.addComponent(topListener, 1);
    }

}
