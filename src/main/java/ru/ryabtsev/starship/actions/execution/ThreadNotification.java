package ru.ryabtsev.starship.actions.execution;

import ru.ryabtsev.starship.actions.Command;

public record ThreadNotification(Object monitor) implements Command {

    @Override
    public void execute() {
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }
}
