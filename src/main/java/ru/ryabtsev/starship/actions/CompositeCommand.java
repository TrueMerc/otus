package ru.ryabtsev.starship.actions;

import java.util.List;
import ru.ryabtsev.starship.exceptions.CommandException;
import ru.ryabtsev.starship.exceptions.CompositeCommandException;


/**
 * The special class for execution of a sequence of several commands.
 */
public class CompositeCommand implements Command {

    private static final String FAILURE_MESSAGE = "Can't execute composite command due to error";

    private final List<? extends Command> commands;

    public CompositeCommand(final List<? extends Command> commands) {
        this.commands = commands;
    }

    public CompositeCommand(final Command ... commands) {
        this.commands = List.of(commands);
    }

    @Override
    public void execute() {
        try {
            commands.forEach(Command::execute);
        } catch (final CommandException e) {
            throw new CompositeCommandException(FAILURE_MESSAGE, e);
        }
    }
}
