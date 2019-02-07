package xyz.justblink.eventbus;

/**
 * Handler for exceptions thrown by the subscriber method
 *
 * @author Kasun Piyumal
 */
public interface SubscriberExceptionHandler {
    void handle(Throwable t, SubscriberExceptionContext context);
}
