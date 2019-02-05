package com.blink.eventbus;

import java.util.Iterator;

public abstract class Dispatcher {
    abstract void dispatch(Object event, Iterator<Subscriber> subscribers);
}
