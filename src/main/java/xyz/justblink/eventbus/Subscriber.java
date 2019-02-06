package xyz.justblink.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Subscriber {
    private final EventBus bus;
    private final Object target;
    private final Method method;

    Subscriber(EventBus bus, Object target, Method method) {
        this.bus = bus;
        this.target = target;
        this.method = method;
        method.setAccessible(true);
    }

    static Subscriber create(EventBus bus, Object target, Method method) {
        return shouldBeThreadSafe(method) ? new ThreadSafeSubscriber(bus, target, method)
                : new Subscriber(bus, target, method);
    }

    private static boolean shouldBeThreadSafe(Method method) {
        return method.getAnnotation(AcceptConcurrentEvents.class) != null;
    }

    final void dispatchEvent(final Object event) {
        bus.getExecutor().execute(() -> {
            try {
                invokeSubscriberMethod(event);
            } catch (InvocationTargetException e) {
                bus.handleSubscriberException(e, context(event));
            }
        });
    }

    void invokeSubscriberMethod(Object event) throws InvocationTargetException {
        if (event == null)
            throw new NullPointerException();

        try {
            method.invoke(target, event);
        } catch (IllegalArgumentException e) {
            throw new Error("Method rejected target/argument: " + event, e);
        } catch (IllegalAccessException e) {
            throw new Error("Method became inaccessible: " + event, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }

    private SubscriberExceptionContext context(Object event) {
        return new SubscriberExceptionContext(bus, event, target, method);
    }

    @Override
    public final int hashCode() {
        return (31 + method.hashCode()) * 31 + System.identityHashCode(target);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Subscriber) {
            Subscriber that = (Subscriber) obj;
            // Use == so that different equal instances will still receive events.
            // We only guard against the case that the same object is registered
            // multiple times
            return target == that.target && method.equals(that.method);
        }
        return false;
    }

    static final class ThreadSafeSubscriber extends Subscriber {

        ThreadSafeSubscriber(EventBus bus, Object target, Method method) {
            super(bus, target, method);
        }

        @Override
        void invokeSubscriberMethod(Object event) throws InvocationTargetException {
            synchronized (this) {
                super.invokeSubscriberMethod(event);
            }
        }
    }
}
