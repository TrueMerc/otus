package ru.ryabtsev.starship.actions.logging;

import ru.ryabtsev.starship.actions.Command;

/**
 * A class for writing an information about exception into the system log.
 */
public class ExceptionLog implements Command {

    private static final String MESSAGE_TEMPLATE = "%s exception has been caught, cause: %s%n";

    private final Exception exception;

    public ExceptionLog(final Exception exception) {
        this.exception = exception;
    }

    @Override
    public void execute() {
        final var type = exception.getClass().getCanonicalName();
        final var cause = exception.getCause();
        System.err.printf((MESSAGE_TEMPLATE), type, cause);
    }
}
