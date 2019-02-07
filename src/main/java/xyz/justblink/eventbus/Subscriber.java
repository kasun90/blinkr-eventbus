package xyz.justblink.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Consists of the subscriber method of a specific object and way to execute it
 *
 * <p>Two subscribers are equivalent when they refer to the same method on the same object (not
 * class). This property is used to ensure that no subscriber method is registered more than once.</p>
 *
 * @author Kasun Piyumal
 */
class Subscriber {
    private final EventBus bus;
    private final Object target;
    private final Method method;

    Subscriber(EventBus bus, Object target, Method method) {
        this.bus = bus;
        this.target = target;
        this.method = method;
        method.setAccessible(true);
    }

    /**
     * @param bus The bus to which the event should be dispatched
     * @param target Target subscriber object
     * @param method Target subscriber method
     * @return a {@code Subscriber} consisting {@code target} and {@code method}
     */
    static Subscriber create(EventBus bus, Object target, Method method) {
        return shouldBeThreadSafe(method) ? new ThreadSafeSubscriber(bus, target, method)
                : new Subscriber(bus, target, method);
    }

    /**
     * Check whether the method should be thread safe by checking the presence of {@link AcceptConcurrentEvents}
     * annotation
     */
    private static boolean shouldBeThreadSafe(Method method) {
        return method.getAnnotation(AcceptConcurrentEvents.class) != null;
    }

    /**
     * Dispatches the {@code event} to the subscriber using the executor provided initially
     */
    final void dispatchEvent(final Object event) {
        bus.getExecutor().execute(() -> {
            try {
                invokeSubscriberMethod(event);
            } catch (InvocationTargetException e) {
                bus.handleSubscriberException(e, context(event));
            }
        });
    }

    /**
     * Invokes the subscriber method. This method can be overridden to make the invocation
     * synchronized.
     */
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

    /**
     * Subscriber that synchronizes invocations of a method to ensure that only one thread may enter
     * the method at a time.
     */
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
