package xyz.justblink.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method as an event subscriber
 *
 * Make sure the method has one and only parameter which is the event to be subscribed to. Otherwise it will not
 * be possible to register in the {@link EventBus}
 *
 * If there is an occasion where EventBus should call the method from multiple threads (If you want to make the
 * method thread-safe), use {@link AcceptConcurrentEvents} annotation too.
 *
 * @author Kasun Piyumal
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
}
