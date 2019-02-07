package xyz.justblink.eventbus;

import java.util.concurrent.Executor;


/**
 *  This {@link EventBus} expects a Executor of your choice to dispatch events. This can enable the events to be
 *  dispatched asynchronously.
 *
 * @author Kasun Piyumal
 */
public class AsyncEventBus extends EventBus {

    /**
     * Creates a new EventBus with the {@code executor} of your choice
     *
     * @param identifier Name for bus, useful in logging if there are multiple buses
     * @param executor Executor to use to dispatch events. You should shutdown this when all events are finished
     *                 posting to the bus.
     */
    public AsyncEventBus(String identifier, Executor executor) {
        super(identifier, executor, ExceptionLogger.INSTANCE, Dispatcher.asyncDispatcher());
    }

    /**
     * Creates a new EventBus with provided {@code executor} and {@link SubscriberExceptionHandler}
     *
     * @param executor Executor to use to dispatch events. You should shutdown this when all events are finished
     *                 posting to the bus.
     * @param subscriberExceptionHandler Handler for exceptions thrown by subscribers methods
     */
    public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
        super("default", executor, subscriberExceptionHandler, Dispatcher.asyncDispatcher());
    }

    /**
     * Creates a new EventBus with the {@code executor} of your choice
     *
     * @param executor Executor to use to dispatch events. You should shutdown this when all events are finished
     *                 posting to the bus.
     */
    public AsyncEventBus(Executor executor) {
        super("default", executor, ExceptionLogger.INSTANCE, Dispatcher.asyncDispatcher());
    }
}
