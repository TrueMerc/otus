package ru.ryabtsev.starship.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.CompositeCommand;
import ru.ryabtsev.starship.actions.execution.ExecutionSoftStop;
import ru.ryabtsev.starship.actions.execution.ExecutionStop;
import ru.ryabtsev.starship.actions.execution.ExecutionStart;
import ru.ryabtsev.starship.actions.execution.ThreadNotification;
import ru.ryabtsev.starship.exceptions.handlers.CommandExceptionHandler;

class SingleThreadExecutorTest {

    private static final int SLEEP_TIME_IN_MILLIS = 10;

    private final CommandQueue commandQueue = new ConcurrentCommandQueue(new CommandExceptionHandler());

    private final SingleThreadExecutor singleThreadExecutor = new SingleThreadExecutor(commandQueue);


    @Test
    void simpleCommandTest() {
        singleThreadExecutor.start();
        final List<Integer> numbers = new ArrayList<>(10);
        IntStream.range(0, 10).forEach(number -> {
            commandQueue.add(new NumberAddition(numbers, number));
            freeze();
        });
        freeze();
        assertEquals(10, numbers.size());
        assertTrue(IntStream.range(0, 10).boxed().allMatch(numbers::contains));
        singleThreadExecutor.stop();
    }

    private void freeze() {
        try {
            Thread.sleep(SLEEP_TIME_IN_MILLIS);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void executionStartCommandBasedTest() {
        new ExecutionStart(singleThreadExecutor).execute();
        assertTrue(singleThreadExecutor.isActive());
        final List<Integer> numbers = new ArrayList<>(10);
        IntStream.range(0, 10).forEach(number -> {
            commandQueue.add(new NumberAddition(numbers, number));
            freeze();
        });
        freeze();
        assertEquals(10, numbers.size());
        assertTrue(IntStream.range(0, 10).boxed().allMatch(numbers::contains));
        singleThreadExecutor.stop();
        assertFalse(singleThreadExecutor.isActive());
    }

    @Test
    void stopCommandBasedTest() {
        // Arrange
        final List<Integer> numbers = new ArrayList<>(10);
        IntStream.range(0, 10).forEach(number -> {
            if (number != 0 && number % 5 == 0) {
                commandQueue.add(new ExecutionStop(singleThreadExecutor, this));
            } else {
                commandQueue.add(new NumberAddition(numbers, number));
            }
        });

        // Act
        synchronized (this) {
            try {
                new ExecutionStart(singleThreadExecutor).execute();
                wait();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        // Assert
        assertFalse(singleThreadExecutor.isActive());
        assertEquals(5, numbers.size());
        assertTrue(IntStream.range(0, 5).boxed().allMatch(numbers::contains));
    }

    @Test
    void softStopCommandBasedTest() {

        // Arrange
        final List<Integer> numbers = new ArrayList<>(10);
        IntStream.range(0, 10).forEach(number -> {
            if (number != 0 && number % 5 == 0) {
                commandQueue.add(new ExecutionSoftStop(singleThreadExecutor, commandQueue));
            } else {
                commandQueue.add(new NumberAddition(numbers, number));
            }
        });
        commandQueue.add(new ThreadNotification(this));

        // Act
        synchronized (this) {
            try {
                new ExecutionStart(singleThreadExecutor).execute();
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Assert
        assertEquals(9, numbers.size());
        assertTrue(IntStream.range(0, 10).filter(number -> number != 5).boxed().allMatch(numbers::contains));
        assertFalse(singleThreadExecutor.isActive());
    }

    private record NumberAddition(List<Integer> numbers, int number) implements Command {

        @Override
        public void execute() {
            numbers.add(number);
        }
    }
}