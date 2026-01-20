package ru.ryabtsev.starship.executors;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.CompositeCommand;
import ru.ryabtsev.starship.actions.execution.ExecutionStart;
import ru.ryabtsev.starship.actions.execution.ExecutionStop;
import ru.ryabtsev.starship.actions.execution.NormalStateRestoration;
import ru.ryabtsev.starship.actions.execution.Redirection;
import ru.ryabtsev.starship.actions.execution.ThreadNotification;
import ru.ryabtsev.starship.exceptions.handlers.CommandExceptionHandler;

class StatefulSingleThreadExecutorTest {

    @Test
    void executorTest() {
        // Arrange:
        final CommandQueue commandQueue = new ConcurrentCommandQueue(new CommandExceptionHandler());
        final CommandQueue anotherCommandQueue = new ConcurrentCommandQueue(new CommandExceptionHandler());
        final List<Integer> numbers = new ArrayList<>();
        // Normal state
        IntStream.range(0, 8).boxed().map(number -> new NumberAddition(numbers, number)).forEach(commandQueue::add);
        final StatefulSingleThreadExecutor executor = new StatefulSingleThreadExecutor(commandQueue);
        // Redirection state
        commandQueue.add(new Redirection(executor, anotherCommandQueue));
        IntStream.range(8, 10).boxed().map(number -> new NumberAddition(numbers, number)).forEach(commandQueue::add);
        // Normal state again
        commandQueue.add(new NormalStateRestoration(executor));
        IntStream.range(10, 12).boxed().map(number -> new NumberAddition(numbers, number)).forEach(commandQueue::add);
        // Execution stop
        commandQueue.add(new CompositeCommand(new ThreadNotification(this), new ExecutionStop(executor)));
        IntStream.range(12, 14).boxed().map(number -> new NumberAddition(numbers, number)).forEach(commandQueue::add);

        // Act:
        synchronized (this) {
            try {
                new ExecutionStart(executor).execute();
                wait();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        //  Assert:
        assertEquals(10, numbers.size());
        assertFalse(anotherCommandQueue.isEmpty());
        assertFalse(commandQueue.isEmpty());
    }


    private record NumberAddition(List<Integer> numbers, int number) implements Command {

        @Override
        public void execute() {
            numbers.add(number);
        }
    }
}