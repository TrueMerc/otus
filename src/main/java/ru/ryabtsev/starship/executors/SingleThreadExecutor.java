package ru.ryabtsev.starship.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that executes commands from special queue in single thread.
 */
public class SingleThreadExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadExecutor.class);

    private final Thread thread;

    private CommandQueue commandQueue;

    private boolean isActive;

    /**
     * Constructs a new single thread executor.
     * @param commandQueue queue which contains commands that should be executed.
     */
    public SingleThreadExecutor(final CommandQueue commandQueue) {
        this.thread = new Thread(() -> {
           while (isActive) {
               commandQueue.execute();
           }
        });
        this.commandQueue = commandQueue;
        this.isActive = false;
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
            logger.info("Thread {} has been successfully stopped", thread.getName());
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
