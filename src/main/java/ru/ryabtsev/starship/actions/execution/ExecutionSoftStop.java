package ru.ryabtsev.starship.actions.execution;

import java.util.function.Supplier;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.executors.SingleThreadExecutor;

public record ExecutionSoftStop(SingleThreadExecutor singleThreadExecutor, CommandQueue commandQueue)
        implements Command {

    @Override
    public void execute() {
        final Supplier<Void> newExecutionStrategy = () -> {
            while(!commandQueue.isEmpty()) {
                commandQueue.execute();
            }
            singleThreadExecutor.stop();
            return null;
        };
        singleThreadExecutor.changeExecutionStrategy(newExecutionStrategy);
    }
}
