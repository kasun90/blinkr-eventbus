package com.blink.eventbus;

import java.util.concurrent.Executor;

public class AsyncEventBus extends EventBus {
    AsyncEventBus(String identifier, Executor executor, SubscriberExceptionHandler exceptionHandler) {
        super(identifier, executor, exceptionHandler, dispatcher);
    }
}
