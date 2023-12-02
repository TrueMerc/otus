package ru.ryabtsev.starship.executors;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.exceptions.handlers.CommandExceptionHandler;

/**
 * A concurrent linked queue based implementation of the CommandQueue interface.
 */
public class ConcurrentCommandQueue implements CommandQueue {

    private final Queue<Command> commands;

    private final CommandExceptionHandler exceptionHandler;

    public ConcurrentCommandQueue(final CommandExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        commands = new ConcurrentLinkedQueue<>();
    }

    /**
     * Adds a command into the execution queue.
     * @param command executable command.
     */
    @Override
    public void add(final Command command) {
        commands.add(command);
    }

    /**
     * Peeks a command which will be executed the next.
     * @return command which wile be executed the next.
     */
    @Override
    public Command peek() {
        return commands.peek();
    }

    /**
     * Executes next command.
     */
    @Override
    public void execute() {
        if (!commands.isEmpty()) {
            final Command command = commands.remove();
            try {
                command.execute();
            } catch (final Exception exception) {
                exceptionHandler.handle(command, exception).ifPresent(commands::add);
            }
        }
    }
}
