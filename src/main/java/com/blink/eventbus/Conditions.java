package com.blink.eventbus;

class Conditions {
    static <T> T checkNonNull(T reference) {
        if (reference == null)
            throw new NullPointerException();
        return reference;
    }
}
