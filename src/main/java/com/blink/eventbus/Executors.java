package com.blink.eventbus;

import java.util.concurrent.Executor;

abstract class Executors {

    static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }

    private static final class DirectExecutor implements Executor {

        static final DirectExecutor INSTANCE = new DirectExecutor();

        public void execute(Runnable command) {
            command.run();
        }

        @Override
        public String toString() {
            return "Executors.directExecutor()";
        }
    }
}
