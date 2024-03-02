package ru.ryabtsev.starship.executors;

import ru.ryabtsev.starship.actions.Command;

/**
 * A basic interface for queues that execute commands sequentially.
 */
public interface CommandQueue {

    void add(Command command);

    Command peek();

    Command poll();

    void execute();

    boolean isEmpty();
}
