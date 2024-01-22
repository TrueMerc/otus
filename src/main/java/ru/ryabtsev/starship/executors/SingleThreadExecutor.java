package ru.ryabtsev.starship.executors;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that executes commands from special queue in single thread.
 */
public class SingleThreadExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadExecutor.class);

    private Thread thread;

    private CommandQueue commandQueue;

    private boolean isActive;

    private Supplier<Void> executionStrategy = () -> {
        while (isActive) {
            commandQueue.execute();
        }
        return null;
    };

    /**
     * Constructs a new single thread executor.
     * @param commandQueue queue which contains commands that should be executed.
     */
    public SingleThreadExecutor(final CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
        thread = new Thread(() -> executionStrategy.get());
        isActive = false;
    }

    /**
     * Starts single thread executor job.
     */
    public void start() {
        isActive = true;
        thread.start();
        logger.info("Thread {} has been successfully started.", thread.getName());
    }

    /**
     * Stops single thread executor job.
     */
    public void stop() {
        isActive = false;
        try {
            thread.join();
            logger.info("Thread {} has been successfully stopped.", thread.getName());
        } catch (final InterruptedException e) {
            logger.error("Can't stop thread in a proper way", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the attribute that determines is this executor active or not.
     * @return the attribute that determines is this executor active or not.
     */
    public boolean isActive() {
        return isActive && thread.isAlive();
    }

    /**
     * Changes execution strategy that is used by the executor.
     */
    public void changeExecutionStrategy(final Supplier<Void> executionStrategy) {
        this.executionStrategy = executionStrategy;
        if (Thread.currentThread().equals(thread)) {
            thread.interrupt();
            this.thread = new Thread(() -> this.executionStrategy.get());
            this.thread.start();
        }
    }
}
