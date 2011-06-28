package org.vaadin.marcus.scrollutil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Server side component for the VScrollUtil widget.
 */
@com.vaadin.ui.ClientWidget(org.vaadin.marcus.scrollutil.client.ui.VScrollUtil.class)
public class ScrollUtil extends AbstractComponent {

    private static final long serialVersionUID = 1L;
    private static Method PROXIMITY_DETECTED_METHOD;
    private Component scrollTarget;
    private int scrollProximity = 200;

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (scrollTarget != null) {
            target.addAttribute("scrollTarget", scrollTarget);
        }

        target.addAttribute("proximity", scrollProximity);

        /* Only enable scroll detection on client side if needed */
        if (hasListeners()) {
            target.addAttribute("scrollListener", true);
        }
    }

    /**
     * Receive and handle events and other variable changes from the client.
     * 
     * {@inheritDoc}
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey("proximityEvent")) {
            fireEvent(new ProximityEvent(this));
        }
    }

    public Component getScrollTarget() {
        return scrollTarget;
    }

    public void scrollTo(Component scrollTarget) {
        this.scrollTarget = scrollTarget;
        requestRepaint();
    }

    /* Event handling */

    /**
     * How many pixels before component visibility a {@link ProximityEvent}
     * should be fired.
     */
    public void setScrollProximity(int scrollProximity) {
        this.scrollProximity = scrollProximity;
    }

    public int getScrollProximity() {
        return scrollProximity;
    }

    /**
     * Event that will be fired when component is within a threshold of being
     * visible.
     * 
     * @author mhellber
     * 
     */
    public static class ProximityEvent extends Component.Event {

        private static final long serialVersionUID = 1L;

        public ProximityEvent(Component source) {
            super(source);
        }
    }

    static {
        try {
            PROXIMITY_DETECTED_METHOD = ProximityEventListener.class
                    .getDeclaredMethod("proximityDetected",
                            new Class[] { ProximityEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in ScrollUtil");
        }
    }

    /**
     * Listener interface for listeners interested in knowing when the ScrolUtil
     * component is about to be visible on the screen.
     * 
     * @author mhellber
     * 
     */
    public interface ProximityEventListener extends Serializable {
        public void proximityDetected(ProximityEvent event);
    }

    public void addListener(ProximityEventListener listener) {

        boolean firstListener = !hasListeners();

        addListener(ProximityEvent.class, listener, PROXIMITY_DETECTED_METHOD);
        if (firstListener) {
            requestRepaint();
        }
    }

    public void removeListener(ProximityEventListener listener) {
        removeListener(ProximityEvent.class, listener,
                PROXIMITY_DETECTED_METHOD);
        if (!hasListeners()) {
            requestRepaint();
        }
    }

    private boolean hasListeners() {
        return getListeners(ProximityEvent.class).size() > 0;
    }
}
