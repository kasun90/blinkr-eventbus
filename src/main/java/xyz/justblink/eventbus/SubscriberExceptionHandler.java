package xyz.justblink.eventbus;

/**
 * Handler for exceptions thrown by the subscriber method
 *
 * @author Kasun Piyumal
 */
public interface SubscriberExceptionHandler {
    /**
     * Handle exceptions thrown by the subscriber method
     */
    void handle(Throwable t, SubscriberExceptionContext context);
}
