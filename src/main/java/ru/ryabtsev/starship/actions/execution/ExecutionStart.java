package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.executors.SingleThreadExecutor;

public record ExecutionStart(SingleThreadExecutor singleThreadExecutor) implements Command {

    @Override
    public void execute() {
        singleThreadExecutor.start();
    }
}
