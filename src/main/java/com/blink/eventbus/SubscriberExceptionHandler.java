package com.blink.eventbus;

public interface SubscriberExceptionHandler {
    void handle(Throwable t, SubscriberExceptionContext context);
}