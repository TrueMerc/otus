package ru.ryabtsev.starship.actions.repetition;

import ru.ryabtsev.starship.actions.Command;

public class Repeat implements Command {

    private final Command command;

    public Repeat(Command command) {
        this.command = command;
    }

    @Override
    public void execute() {
        command.execute();
    }
}
