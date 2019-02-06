package com.blink.eventbus;

import java.util.concurrent.Executor;

public class AsyncEventBus extends EventBus {

    public AsyncEventBus(String identifier, Executor executor) {
        super(identifier, executor, ExceptionLogger.INSTANCE, Dispatcher.asyncDispatcher());
    }

    public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
        super("default", executor, subscriberExceptionHandler, Dispatcher.asyncDispatcher());
    }

    public AsyncEventBus(Executor executor) {
        super("default", executor, ExceptionLogger.INSTANCE, Dispatcher.asyncDispatcher());
    }
}
