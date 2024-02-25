package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.executors.StatefulSingleThreadExecutor;

public class Redirection  implements Command {

    private final StatefulSingleThreadExecutor executor;

    private final CommandQueue commandQueue;

    public Redirection(final StatefulSingleThreadExecutor executor, final CommandQueue commandQueue) {
        this.executor = executor;
        this.commandQueue = commandQueue;
    }

    @Override
    public void execute() {
        executor.changeState(new StatefulSingleThreadExecutor.RedirectionState(commandQueue));
    }
}
