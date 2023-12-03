package ru.ryabtsev.starship.exceptions.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import ru.ryabtsev.starship.actions.Command;

/**
 * A class that provides logic
 */
public class CommandExceptionHandler {

    private final Map<
            Class<? extends Command>,
            Map<Class<? extends Exception>, BiFunction<Command, Exception, Command>>> handlers;

    public CommandExceptionHandler() {
        this.handlers = new HashMap<>();
    }

    public CommandExceptionHandler register(
            final Class<? extends Command> commandClass,
            final Class<? extends Exception> exceptionClass,
            final BiFunction<Command, Exception, Command> handler) {
        final var exceptionHandlers = handlers.getOrDefault(commandClass, new HashMap<>(32));
        exceptionHandlers.put(exceptionClass, handler);
        handlers.put(commandClass, exceptionHandlers);
        return this;
    }

    public Optional<Command> handle(final Command command, final Exception exception) {
        return Optional.ofNullable(handlers.get(command.getClass()))
                .map(exceptionHandlers -> exceptionHandlers.get(exception.getClass()))
                .map(function -> function.apply(command, exception));
    }
}
