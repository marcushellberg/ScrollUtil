package org.vaadin.marcus.scrollutil.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

/**
 * Client side widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VScrollUtil extends SimplePanel implements Paintable {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-scroll-util";

    /**
     * Default amount of pixels before the component is shown before event is
     * triggered.
     */
    protected int alertProximity = 200;
    protected int previousScrollTop = -1;

    /** The client side widget identifier */
    protected String paintableId;

    /** Reference to the server connection object. */
    protected ApplicationConnection client;

    private ScrollHandler scrollHandler = new ScrollHandler() {

        public void onWindowScroll(ScrollEvent event) {
            checkProximity();
        }
    };

    private HandlerRegistration scrollHandlerRegistration;

    private boolean inited = false;

    /**
     * The constructor should first call super() to initialize the component and
     * then handle any initialization relevant to Vaadin.
     */
    public VScrollUtil() {
        super();
        setStyleName(CLASSNAME);
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
    }

    /**
     * Called whenever an update is received from the server
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        this.client = client;
        paintableId = uidl.getId();

        if (uidl.hasAttribute("scrollTarget")) {
            scrollTo((Widget) client.getPaintable(uidl
                    .getStringAttribute("scrollTarget")));
        }

        if (uidl.hasAttribute("scrollListener")) {
            if (uidl.hasAttribute("proximity")) {
                alertProximity = uidl.getIntAttribute("proximity");
            }
            if (!inited) {
                previousScrollTop = Window.getScrollTop();
            }

            if (uidl.getBooleanAttribute("scrollListener")) {
                addScrollListener();
            } else {
                removeScrollListener();
            }
        }

    }

    protected void checkProximity() {
        int scrollTop = Window.getScrollTop();
        boolean goingDown = scrollTop > previousScrollTop;
        previousScrollTop = scrollTop;

        if (goingDown) {
            int alertThreshold = getElement().getAbsoluteTop() - alertProximity;
            if (alertThreshold < 0) {
                alertThreshold = 0;
            }
            int windowBottom = Window.getScrollTop() + Window.getClientHeight();

            debug("Going down. WindowBottom: " + windowBottom
                    + ", alertThreshold: " + alertThreshold);
            if (windowBottom > alertThreshold) {
                debug("Fired event");
                fireProximityEvent();
            }
        } else {
            int alertThreshold = getElement().getAbsoluteTop()
                    + getElement().getOffsetHeight() + alertProximity;

            debug("Going up. ScrollTop: " + scrollTop + ", alertThreshold: "
                    + alertThreshold);

            if (scrollTop < alertThreshold) {
                debug("Fired event");
                fireProximityEvent();
            }
        }

    }

    private void debug(String message) {
        // client.updateVariable(paintableId, "debugMessage", message, true);
    }

    protected void fireProximityEvent() {
        client.updateVariable(paintableId, "proximityEvent", true, true);
        removeScrollListener();
    }

    private void addScrollListener() {
        // See if component is already visible when adding, if so just fire
        // event and do not add scroll detection
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            public void execute() {
                if (!componentVisible()) {
                    if (scrollHandlerRegistration == null) {
                        scrollHandlerRegistration = Window
                                .addWindowScrollHandler(scrollHandler);
                        VConsole.log("Added scroll handler");
                    }
                }
            }
        });
    }

    private boolean componentVisible() {
        int top = getElement().getAbsoluteTop();
        int scrollTop = Window.getScrollTop();
        int clientHeight = Window.getClientHeight();

        if (top >= scrollTop && top <= scrollTop + clientHeight) {
            debug("Component visible, firing event. Top: " + top
                    + ", scrollTop:" + scrollTop + ", clientHeight: "
                    + clientHeight);
            fireProximityEvent();
            return true;
        } else {
            return false;
        }
    }

    private void removeScrollListener() {
        if (scrollHandlerRegistration != null) {
            scrollHandlerRegistration.removeHandler();
            scrollHandlerRegistration = null;
        }
    }

    private void scrollTo(Widget scrollTarget) {
        int clientHeight = Window.getClientHeight();
        int targetPos = scrollTarget.getAbsoluteTop();
        // We want to scroll the component as close to the center of the screen
        // as possible
        final int scrollToPos = targetPos - (clientHeight / 2);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            public void execute() {
                Window.scrollTo(0, scrollToPos);
            }
        });
    }

    @Override
    protected void onDetach() {
        if (scrollHandlerRegistration != null) {
            scrollHandlerRegistration.removeHandler();
            scrollHandlerRegistration = null;
        }

        super.onDetach();
    }
}