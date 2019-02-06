package com.blink.eventbus;

public class EventTest {
    static class TestEvent {}
    static class TestEventConcurrent{}

    @Subscribe
    public void onTestEvent(TestEvent e) {
        System.out.println("EventTest event received");
    }

    @Subscribe
    public void onTestEventCon(TestEventConcurrent e) {
        System.out.println("Conccurent event received on thread: " + Thread.currentThread().getName());
    }

}
