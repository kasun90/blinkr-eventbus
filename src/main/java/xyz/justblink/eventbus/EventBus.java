package xyz.justblink.eventbus;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This will dispatch event to its subscribers and provides a way to register objects themselves as subscribers.
 *
 * <p>This is implemented by mimicking features of Google's
 * <a href="https://github.com/google/guava/wiki/EventBusExplained">Guava EventBus</a>. The EventBus makes use of
 * a publisher-subscriber style event handling mechanism. This features enables components to have a loose coupling
 * between them. That is, components which initiate the event and the components which receives the events don't have
 * to be aware of each other. This is intended for communication only within the particular process (Not for
 * inter-process communication).</p>
 *
 * <h2>Receive events</h2>
 *
 * <p>To receive events, an object should satisfy following conditions: </p>
 *
 * <ol>
 *     <li>Expose a public method which one accepts only one argument of the type of the desired event</li>
 *     <li>Mark it with {@link Subscribe} annotation</li>
 *     <li>Pass itself to EventBus instance's {@link #register(Object)} method</li>
 * </ol>
 *
 *
 * <h2>Posting events</h2>
 *
 * <p>Pass the desired event to EventBus instance's {@link #post(Object)} method.</p>
 *
 * <p>Currently this will only dispatch the event to the subscribers with exact class match. (Not for subscribers of
 * superclasses and interfaces which that class implements)</p>
 *
 * <p>When an event is posted to bus, it will begin calling subscribers in sequence. Because of that, subscriber
 * methods should be quick. If you want a subscriber method to run for a long time, spawn it in a different thread.
 * You can use {@link AsyncEventBus} for this purpose</p>
 *
 * <h2>More about subscriber methods</h2>
 *
 * <p>Subscribers should not generally throw. It should be handled by try catch. Default behavior is just to log it.</p>
 *
 * <p>The EventBus guarantees that it will not call a subscriber method from multiple threads
 *  simultaneously, unless the method explicitly allows it by bearing the {@link
 *  AcceptConcurrentEvents} annotation. If this annotation is not present, subscriber methods need not
 *  worry about being reentrant, unless also called from outside the EventBus.</p>
 *
 *  <p>To override this behavior (allow subscriber method to be reentrant), provide
 *  {@link Dispatcher#immediateDispatcher()} as the dispatcher of choice.</p>
 *
 *  <h2>Dead Events</h2>
 *
 *  <p>If there are no subscribers for an event posted, it it considered as dead. They are posted again
 *  by wrapping inside a {@link DeadEvent} and can be caught by subscribing to Dead Events</p>
 *
 *  <p>This class is thread safe</p>
 *
 * @author Kasun Piyumal
 */
public class EventBus {
    private static final Logger logger = Logger.getLogger(EventBus.class.getName());
    private final String identifier;
    private final Executor executor;
    private final SubscriberExceptionHandler exceptionHandler;
    private final Dispatcher dispatcher;

    private final SubscriberRegistry registry = new SubscriberRegistry(this);

    /**
     * Creates an EventBus named "default"
     */
    public EventBus() {
        this("default", Executors.directExecutor(), ExceptionLogger.INSTANCE, Dispatcher.perThreadDispatcher());
    }

    /**
     * Creates a new EventBsus with {@code identifier}
     *
     * @param identifier name for the bus, can be useful in logging if there are multiple buses
     */
    public EventBus(String identifier) {
        this(identifier, Executors.directExecutor(), ExceptionLogger.INSTANCE, Dispatcher.perThreadDispatcher());
    }

    /**
     * Creates a new EventBus with provided {@link SubscriberExceptionHandler}
     *
     * @param exceptionHandler Handler for exceptions thrown by subscribers methods
     */
    public EventBus(SubscriberExceptionHandler exceptionHandler) {
        this("default", Executors.directExecutor(), exceptionHandler, Dispatcher.perThreadDispatcher());
    }

    /**
     * Creates a new EventBus with provided {@link Dispatcher}
     *
     * @param dispatcher Dispatcher of choice for the bus. Be careful when using this, because methods may need to
     *                   be reentrant for some dispatchers
     */
    public EventBus(Dispatcher dispatcher) {
        this("default", Executors.directExecutor(), ExceptionLogger.INSTANCE, dispatcher);
    }

    /**
     * Creates a new EventBus with provided {@link SubscriberExceptionHandler} and {@link Dispatcher}
     *
     * @param exceptionHandler Handler for exceptions thrown by subscribers methods
     * @param dispatcher Dispatcher of choice for the bus. Be careful when using this, because methods may need to
     *                   be reentrant for some dispatchers
     */
    public EventBus(SubscriberExceptionHandler exceptionHandler, Dispatcher dispatcher) {
        this("default", Executors.directExecutor(), exceptionHandler, dispatcher);
    }

    EventBus(String identifier, Executor executor, SubscriberExceptionHandler exceptionHandler, Dispatcher dispatcher) {
        this.identifier = identifier;
        this.executor = executor;
        this.exceptionHandler = exceptionHandler;
        this.dispatcher = dispatcher;
    }

    public String getIdentifier() {
        return identifier;
    }

    Executor getExecutor() {
        return executor;
    }

    public void register(Object object) {
        registry.register(object);
    }

    public void unregister(Object object) {
        registry.unregister(object);
    }

    public void post(Object event) {
        Iterator<Subscriber> allSubscribers = registry.getAllSubscribers(event);
        if (allSubscribers.hasNext())
            dispatcher.dispatch(event, allSubscribers);
        else if (!(event instanceof DeadEvent))
            post(new DeadEvent(this, event));
    }

    void handleSubscriberException(Throwable e, SubscriberExceptionContext context) {
        try {
            exceptionHandler.handle(e, context);
        } catch (Throwable e2) {
            logger.log(
                    Level.SEVERE,
                    String.format("Exception %s thrown while handling exception: %s", e2, e),
                    e2);
        }
    }

    static final class ExceptionLogger implements SubscriberExceptionHandler {

        static final ExceptionLogger INSTANCE = new ExceptionLogger();

        private static Logger logger(SubscriberExceptionContext context) {
            return Logger.getLogger(EventBus.class.getName() + "." + context.getEventBus().getIdentifier());
        }

        private static String message(SubscriberExceptionContext context) {
            Method method = context.getSubscriberMethod();
            return "Exception thrown by subscriber method "
                    + method.getName()
                    + '('
                    + method.getParameterTypes()[0].getName()
                    + ')'
                    + " on subscriber "
                    + context.getSubscriber()
                    + " when dispatching event: "
                    + context.getEvent();
        }

        public void handle(Throwable t, SubscriberExceptionContext context) {
            Logger logger = logger(context);
            if (logger.isLoggable(Level.SEVERE))
                logger.log(Level.SEVERE, message(context));
        }
    }
}
