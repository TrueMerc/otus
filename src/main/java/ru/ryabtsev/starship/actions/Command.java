package ru.ryabtsev.starship.actions;

/**
 * The basic interface for all actions that can be considered as implementations of 'Command' pattern.
 */
public interface Command {

    /**
     * Executes a command internal logic.
     */
    void execute();
}
