package ru.ryabtsev.starship.actions.repetition;

import ru.ryabtsev.starship.actions.Command;

public class SecondTimeRepeat implements Command {

    private final Repeat repeat;

    public SecondTimeRepeat(final Command command) {
        if (!(command instanceof Repeat)) {
            throw new IllegalArgumentException("Command has inappropriate type " + command.getClass().getSimpleName());
        }
        repeat = (Repeat) command;
    }

    @Override
    public void execute() {
        repeat.execute();
    }
}
