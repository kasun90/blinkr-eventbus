package xyz.justblink.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * When this annotation is added to a method, it implies that the EventBus may call this method from multiple threads
 * and the method is made to be thread safe
 *
 * @author Kasun Piyumal
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AcceptConcurrentEvents {
}
