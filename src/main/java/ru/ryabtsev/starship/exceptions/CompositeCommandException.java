package ru.ryabtsev.starship.exceptions;

/**
 * CompositeCommandException is the class for those exceptions that can be thrown when one of the commands of
 * CompositeCommand threw exception.
 */
public class CompositeCommandException extends CommandException {

    public CompositeCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
