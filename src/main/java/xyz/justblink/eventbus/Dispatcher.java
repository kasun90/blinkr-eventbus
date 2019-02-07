package xyz.justblink.eventbus;


import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static xyz.justblink.eventbus.Conditions.checkNonNull;

/**
 * Handler for maintaining the order of events to be dispatched. It is different from the {@code Executor} used in
 * {@link EventBus}. This controls only the order of the events to be dispatched. On the other hand,
 * {@code Executor} controls how the subscriber's method is called while dispatching the event
 *
 * @author Kasun Piyumal
 */
abstract class Dispatcher {

    /**
     * Returns a dispatcher that queues events that are posted reentrantly on a thread that is already
     * dispatching an event, guaranteeing that all events posted on a single thread are dispatched to
     * all subscribers in the order they are posted.
     *
     * <p>When all subscribers are dispatched to using a <i>direct</i> executor (which dispatches on
     * the same thread that posts the event), this yields a breadth-first dispatch order on each
     * thread. That is, all subscribers to a single event A will be called before any subscribers to
     * any events B and C that are posted to the event bus by the subscribers to A.
     *
     * This doc stub copied from Google's
     * <a href="https://github.com/google/guava/wiki/EventBusExplained">Guava EventBus</a>.
     */
    static Dispatcher perThreadDispatcher() {
        return new PerThreadQueuedDispatcher();
    }

    /**
     * Returns a dispatcher that queues events that are posted in a single global queue. This behavior
     * matches the original behavior of AsyncEventBus exactly, but is otherwise not especially useful.
     * For async dispatch, an {@linkplain #immediateDispatcher()}  immediate} dispatcher should generally be
     * preferable.
     *
     * This doc stub copied from Google's
     * <a href="https://github.com/google/guava/wiki/EventBusExplained">Guava EventBus</a>.
     */
    static Dispatcher asyncDispatcher() {
        return new AsyncDispatcher();
    }

    /**
     * Returns a dispatcher that dispatches events to subscribers immediately as they're posted
     * without using an intermediate queue to change the dispatch order. This is effectively a
     * depth-first dispatch order, vs. breadth-first when using a queue.
     *
     * This doc stub copied from Google's
     * <a href="https://github.com/google/guava/wiki/EventBusExplained">Guava EventBus</a>.
     */
    public static Dispatcher immediateDispatcher() {
        return new ImmediateDispatcher();
    }

    /**
     * @param event Event to be dispatched
     * @param subscribers Corresponding subscribers to the {@code event}
     */
    abstract void dispatch(Object event, Iterator<Subscriber> subscribers);

    /**
     * Implementing {@link #perThreadDispatcher()} dispatcher
     */
    private static final class PerThreadQueuedDispatcher extends Dispatcher {

        /** Per-thread queue of events to dispatch. */
        private final ThreadLocal<Queue<Event>> queue =
                ThreadLocal.withInitial(ArrayDeque::new);

        /** Per-thread dispatch state, used to avoid reentrant event dispatching. */
        private final ThreadLocal<Boolean> dispatching =
                ThreadLocal.withInitial(() -> false);

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            checkNonNull(event);
            checkNonNull(subscribers);
            Queue<Event> queueForThread = queue.get();
            queueForThread.offer(new Event(event, subscribers));

            if (!dispatching.get()) {
                dispatching.set(true);
                try {
                    Event nextEvent;
                    while ((nextEvent = queueForThread.poll()) != null) {
                        while (nextEvent.subscribers.hasNext()) {
                            nextEvent.subscribers.next().dispatchEvent(nextEvent.event);
                        }
                    }
                } finally {
                    dispatching.remove();
                    queue.remove();
                }
            }
        }

        private static final class Event {
            private final Object event;
            private final Iterator<Subscriber> subscribers;

            private Event(Object event, Iterator<Subscriber> subscribers) {
                this.event = event;
                this.subscribers = subscribers;
            }
        }
    }

    /**
     * Implementing {@link #asyncDispatcher()} dispatcher
     */
    private static final class AsyncDispatcher extends Dispatcher {
        /**
         * Common event queue
         */
        private final ConcurrentLinkedQueue<EventWithSubscriber> queue =
                new ConcurrentLinkedQueue<>();

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            checkNonNull(event);
            while (subscribers.hasNext()) {
                queue.add(new EventWithSubscriber(event, subscribers.next()));
            }

            EventWithSubscriber e;
            while ((e = queue.poll()) != null) {
                e.subscriber.dispatchEvent(e.event);
            }
        }

        private static final class EventWithSubscriber {
            private final Object event;
            private final Subscriber subscriber;

            private EventWithSubscriber(Object event, Subscriber subscriber) {
                this.event = event;
                this.subscriber = subscriber;
            }
        }
    }

    /**
     * Implementing {@link #immediateDispatcher()} dispatcher
     */
    private static final class ImmediateDispatcher extends Dispatcher {
        private static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            checkNonNull(event);
            while (subscribers.hasNext()) {
                subscribers.next().dispatchEvent(event);
            }
        }
    }
}
