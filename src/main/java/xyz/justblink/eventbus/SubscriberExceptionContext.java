package xyz.justblink.eventbus;

import java.lang.reflect.Method;

/**
 * Exception context to be passed to {@link SubscriberExceptionHandler}
 *
 * @author Kasun Piyumal
 */
class SubscriberExceptionContext {
    private final EventBus eventBus;
    private final Object event;
    private final Object subscriber;
    private final Method subscriberMethod;

    /**
     * @param eventBus The {@link EventBus} that handled the event and the subscriber. Can post another event based
     *                 on the error
     * @param event The original event dispatched to bus when the exception occurred
     * @param subscriber The source of the exception
     * @param subscriberMethod the subscribed method
     */
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
