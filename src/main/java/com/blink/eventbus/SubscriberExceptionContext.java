package com.blink.eventbus;

import java.lang.reflect.Method;

public class SubscriberExceptionContext {
    private final EventBus eventBus;
    private final Object event;
    private final Object subscriber;
    private final Method subscriberMethod;

    public SubscriberExceptionContext(EventBus eventBus, Object event, Object subscriber, Method subscriberMethod) {
        this.eventBus = eventBus;
        this.event = event;
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Object getEvent() {
        return event;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public Method getSubscriberMethod() {
        return subscriberMethod;
    }
}
