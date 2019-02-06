package com.blink.eventbus;

import java.lang.reflect.Method;

class SubscriberExceptionContext {
    private final EventBus eventBus;
    private final Object event;
    private final Object subscriber;
    private final Method subscriberMethod;

    SubscriberExceptionContext(EventBus eventBus, Object event, Object subscriber, Method subscriberMethod) {
        this.eventBus = eventBus;
        this.event = event;
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

    EventBus getEventBus() {
        return eventBus;
    }

    Object getEvent() {
        return event;
    }

    Object getSubscriber() {
        return subscriber;
    }

    Method getSubscriberMethod() {
        return subscriberMethod;
    }
}
