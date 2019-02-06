package com.blink.eventbus;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

class SubscriberRegistry {
    private final EventBus bus;
    private final Map<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers = new ConcurrentHashMap<>();

    SubscriberRegistry(EventBus bus) {
        this.bus = bus;
    }

    void register(Object listener) {
        Map<Class<?>, Collection<Subscriber>> listenerMethods = findAllSubscribers(listener);

        for (Map.Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.entrySet()) {
            Class<?> type = entry.getKey();
            Collection<Subscriber> methodsInListener = entry.getValue();

            Collection<Subscriber> eventSubscribers = subscribers.computeIfAbsent(type,
                    aClass -> new CopyOnWriteArraySet<>());
            eventSubscribers.addAll(methodsInListener);
        }
    }

    void unregister(Object listener) {
        Map<Class<?>, Collection<Subscriber>> listenerMethods = findAllSubscribers(listener);

        for (Map.Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.entrySet()) {
            Class<?> type = entry.getKey();
            Collection<Subscriber> listenerMethodsForType = entry.getValue();

            CopyOnWriteArraySet<Subscriber> subscribers = this.subscribers.get(type);

            if (subscribers != null)
                subscribers.removeAll(listenerMethodsForType);

        }
    }

    private Map<Class<?>, Collection<Subscriber>> findAllSubscribers(Object listener) {
        Map<Class<?>, Collection<Subscriber>> subscriberMap = new HashMap<>();
        Class<?> currentClass = listener.getClass();

        while (currentClass != null) {
            for (Method method : currentClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
                    if (method.getParameterCount() != 1) {
                        throw new IllegalArgumentException(MessageFormat.format("Method {0} has @Subscribe annotation " +
                                        "but has {1} parameters. Subscriber methods must have exactly 1 parameter",
                                method, method.getParameterCount()));
                    }

                    Class<?> type = method.getParameterTypes()[0];
                    Collection<Subscriber> subscribers = subscriberMap.computeIfAbsent(type, aClass -> new ArrayList<>());
                    subscribers.add(Subscriber.create(bus, listener, method));
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return subscriberMap;
    }

    Iterator<Subscriber> getAllSubscribers(Object event) {
        CopyOnWriteArraySet<Subscriber> eventSubscribers = this.subscribers.get(event.getClass());
        if (eventSubscribers != null) {
            ArrayList<Subscriber> subscribers = new ArrayList<>(eventSubscribers.size());
            subscribers.addAll(eventSubscribers);
            return subscribers.iterator();
        } else {
            return Collections.emptyIterator();
        }
    }

}
