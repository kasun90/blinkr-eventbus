package xyz.justblink.eventbus;

import java.util.concurrent.Executor;

/**
 * Executors that are used to dispatch events. Must be used in cases where external executor is not
 *  provided for {@link EventBus}
 *
 * @author Kasun Piyumal
 */
abstract class Executors {

    /**
     * Returns an Executor to be used in EventBus. Not intended to use with AsyncEventBus
     *
     * @return an instance of {@link DirectExecutor}
     */
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
