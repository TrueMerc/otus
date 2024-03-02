package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.SingleThreadExecutor;
import ru.ryabtsev.starship.executors.Stoppable;

/**
 * The class that implements command which stops execution immediately.
 * @param stoppable executor that will be stopped after an execution of this command.
 */
public record ExecutionStop(Stoppable stoppable) implements Command {

    @Override
    public void execute() {
        stoppable.stop();
    }
}
