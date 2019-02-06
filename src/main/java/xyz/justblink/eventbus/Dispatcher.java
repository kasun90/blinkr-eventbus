package xyz.justblink.eventbus;


import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract class Dispatcher {

    static Dispatcher perThreadDispatcher() {
        return new PerThreadQueuedDispatcher();
    }

    static Dispatcher asyncDispatcher() {
        return new AsyncDispatcher();
    }

    abstract void dispatch(Object event, Iterator<Subscriber> subscribers);

    private static final class PerThreadQueuedDispatcher extends Dispatcher {

        private final ThreadLocal<Queue<Event>> queue =
                ThreadLocal.withInitial(ArrayDeque::new);

        /** Per-thread dispatch state, used to avoid reentrant event dispatching. */
        private final ThreadLocal<Boolean> dispatching =
                ThreadLocal.withInitial(() -> false);

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            Conditions.checkNonNull(event);
            Conditions.checkNonNull(subscribers);
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

    private static final class AsyncDispatcher extends Dispatcher {
        private final ConcurrentLinkedQueue<EventWithSubscriber> queue =
                new ConcurrentLinkedQueue<>();

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            Conditions.checkNonNull(event);
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
}
