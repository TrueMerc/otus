package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.StatefulSingleThreadExecutor;

public class NormalStateRestoration implements Command {

    private final StatefulSingleThreadExecutor executor;

    public NormalStateRestoration(StatefulSingleThreadExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute() {
        executor.changeState(new StatefulSingleThreadExecutor.NormalState());
    }
}
