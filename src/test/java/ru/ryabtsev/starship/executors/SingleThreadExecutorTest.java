package ru.ryabtsev.starship.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.execution.ExecutionStart;
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
        final List<Integer> numbers = new ArrayList<>(10);
        IntStream.range(0, 10).forEach(number -> {
            commandQueue.add(new NumberAddition(numbers, number));
            freeze();
        });
        freeze();

        singleThreadExecutor.stop();
    }


    private record NumberAddition(List<Integer> numbers, int number) implements Command {

        @Override
        public void execute() {
            numbers.add(number);
        }
    }
}