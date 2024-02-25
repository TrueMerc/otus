package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.SingleThreadExecutor;
import ru.ryabtsev.starship.executors.Startable;

/**
 * The class that implements command which starts new single thread executor.
 * @param startable executor that will be started during an execution of this command.
 */
public record ExecutionStart(Startable startable) implements Command {

    @Override
    public void execute() {
        startable.start();
    }
}
