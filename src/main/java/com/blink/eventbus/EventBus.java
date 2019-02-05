package com.blink.eventbus;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBus {
    private static final Logger logger = Logger.getLogger(EventBus.class.getName());
    private final String identifier;
    private final Executor executor;
    private final SubscriberExceptionHandler exceptionHandler;
    private final Dispatcher dispatcher;

    private final SubscriberRegistry registry = new SubscriberRegistry(this);

    public EventBus() {
        this("default", Executors.directExecutor(), ExceptionLogger.INSTANCE, null);
    }

    public EventBus(String identifier) {
        this(identifier, Executors.directExecutor(), ExceptionLogger.INSTANCE, null);
    }

    public EventBus(SubscriberExceptionHandler exceptionHandler) {
        this("default", Executors.directExecutor(), exceptionHandler, null);
    }

    EventBus(String identifier, Executor executor, SubscriberExceptionHandler exceptionHandler, Dispatcher dispatcher) {
        this.identifier = identifier;
        this.executor = executor;
        this.exceptionHandler = exceptionHandler;
        this.dispatcher = dispatcher;
    }

    private String getIdentifier() {
        return identifier;
    }

    public Executor getExecutor() {
        return executor;
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
