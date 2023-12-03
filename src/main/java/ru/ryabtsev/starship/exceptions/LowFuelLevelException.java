package ru.ryabtsev.starship.exceptions;

public class LowFuelLevelException extends CommandException {

    public LowFuelLevelException(String message) {
        super(message);
    }
}
