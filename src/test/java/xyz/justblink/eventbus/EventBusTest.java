package xyz.justblink.eventbus;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.Executors;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class EventBusTest {

    EventBus bus;

    @BeforeAll
    public void init() {
        bus = new EventBus();
    }

    @Test
    public void singleEventTest() {
        bus.register(new EventTest());
        bus.post(new EventTest.TestEvent());
        bus.post(new EventTest.TestEventConcurrent());
    }

    @Test
    public void asyncEventTest() {
        EventBus asyncBus =  new AsyncEventBus(Executors.newCachedThreadPool());
        asyncBus.register(new EventTest());
        asyncBus.post(new EventTest.TestEventConcurrent());
        System.out.println("Posted on thread: " + Thread.currentThread().getName());
    }

    @Test
    public void asyncEventTest2() {
        EventBus bus = new AsyncEventBus(Executors.newCachedThreadPool());
        bus.register(new BusInsideEventTest(bus));
        bus.register(new EventTest());
        bus.post(new BusInsideEventTest.InitEvent());
        System.out.println("Posted on thread: " + Thread.currentThread().getName());
    }
}