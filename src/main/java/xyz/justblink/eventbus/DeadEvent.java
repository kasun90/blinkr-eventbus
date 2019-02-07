package xyz.justblink.eventbus;

/**
 * This event is used to wrap event which are posted to bus and doesn't to have a corresponding subscriber
 * method
 *
 * <p>This is useful when debugging and getting all your communication in place</p>
 *
 * @author Kasun Piyumal
 */
public class DeadEvent {
    private final Object source;
    private final Object event;

    /**
     * Creates a new DeadEvent
     *
     * @param source The source from which initiated the DeadEvent, typically {@link EventBus}
     * @param event The event that couldn't be delivered
     */
    DeadEvent(Object source, Object event) {
        this.source = source;
        this.event = event;
    }


    /**
     * Returns the object that generated this event. <em>not</em> the object which posted the wrapped event
     *
     * @return The source that generated the event
     */
    public Object getSource() {
        return source;
    }

    /**
     * Returns the event which has no registered subscribers in the event bus
     *
     * @return the event which couldn't be delivered (marked as dead)
     */
    public Object getEvent() {
        return event;
    }
}
