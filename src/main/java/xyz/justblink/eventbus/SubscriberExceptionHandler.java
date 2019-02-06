package xyz.justblink.eventbus;

public interface SubscriberExceptionHandler {
    void handle(Throwable t, SubscriberExceptionContext context);
}
