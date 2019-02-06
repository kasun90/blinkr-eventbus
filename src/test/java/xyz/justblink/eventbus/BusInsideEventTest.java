package xyz.justblink.eventbus;

public class BusInsideEventTest {
    private EventBus bus;
    static class InitEvent{}

    public BusInsideEventTest(EventBus bus) {
        this.bus = bus;
    }

    @Subscribe
    public void iniEvent(InitEvent e) {
        System.out.println("Init event received on thread: " + Thread.currentThread().getName());
        bus.post(new EventTest.TestEventConcurrent());
    }

}
