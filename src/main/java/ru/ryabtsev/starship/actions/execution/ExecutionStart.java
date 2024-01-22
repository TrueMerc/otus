package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.SingleThreadExecutor;

/**
 * The class that implements command which starts new single thread executor.
 * @param singleThreadExecutor executor that will be started during an execution of this command.
 */
public record ExecutionStart(SingleThreadExecutor singleThreadExecutor) implements Command {

    @Override
    public void execute() {
        singleThreadExecutor.start();
    }
}
