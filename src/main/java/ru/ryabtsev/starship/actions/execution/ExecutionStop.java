package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.SingleThreadExecutor;

/**
 * The class that implements command which stops execution immediately.
 * @param singleThreadExecutor executor that will be stopped after an execution of this command.
 */
public record ExecutionStop(SingleThreadExecutor singleThreadExecutor, Object monitor) implements Command {

    @Override
    public void execute() {
        synchronized (monitor) {
            monitor.notifyAll();
        }
        singleThreadExecutor.stop();
    }
}
