package com.blink.eventbus;


import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;

public class EventBusTest {

    EventBus bus;

    @Before
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
}