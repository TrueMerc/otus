package ru.ryabtsev.starship.executors;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.execution.ExecutionStop;
import ru.ryabtsev.starship.actions.execution.NormalStateRestoration;

public class StatefulSingleThreadExecutor implements Startable, Stoppable {

    private static final Logger logger = LoggerFactory.getLogger(StatefulSingleThreadExecutor.class);

    private ExecutionState state;

    private CommandQueue commandQueue;

    private Thread thread;

    private Supplier<Void> executionStrategy = () -> {
        while (state.isActive()) {
            state.execute(commandQueue.poll());
        }
        return null;
    };

    /**
     * Constructs a new stateful single thread executor.
     * @param commandQueue queue which contains commands that should be executed.
     */
    public StatefulSingleThreadExecutor(final CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
        thread = new Thread(() -> executionStrategy.get());
        state = new NormalState();
    }

    @Override
    public void start() {
        thread.start();
        logger.info("Thread {} has been successfully started.", thread.getName());
    }

    @Override
    public void stop() {
        try {
            thread.join();
            logger.info("Thread {} has been successfully stopped.", thread.getName());
            state = new StoppedState();
        } catch (final InterruptedException e) {
            logger.error("Can't stop thread in a proper way", e);
            throw new IllegalStateException(e);
        }
    }

    public void changeState(final ExecutionState executionState) {
        state = executionState;
    }


    public interface ExecutionState {

        boolean isActive();

        void execute(Command command);
    }

    public static class NormalState implements ExecutionState {

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void execute(final Command command) {
            command.execute();
        }
    }

    public static class RedirectionState implements ExecutionState {

        private final CommandQueue redirectedCommandQueue;

        public RedirectionState(final CommandQueue redirectedCommandQueue) {
            this.redirectedCommandQueue = redirectedCommandQueue;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void execute(final Command command) {
            if (command instanceof ExecutionStop || command instanceof NormalStateRestoration) {
                command.execute();
            } else {
                redirectedCommandQueue.add(command);
            }
        }
    }

    public static class StoppedState implements ExecutionState {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void execute(final Command command) {
            throw new IllegalStateException("Can't execute anything when the queue is stopped");
        }
    }
}
