package com.blink.eventbus;

public class DeadEvent {
    private final Object source;
    private final Object event;

    DeadEvent(Object source, Object event) {
        this.source = source;
        this.event = event;
    }

    public Object getSource() {
        return source;
    }

    public Object getEvent() {
        return event;
    }
}
