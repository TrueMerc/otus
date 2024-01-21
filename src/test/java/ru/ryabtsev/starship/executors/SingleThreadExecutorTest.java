package ru.ryabtsev.starship.executors;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.exceptions.handlers.CommandExceptionHandler;

class SingleThreadExecutorTest {

    private final int SLEEP_TIME_IN_MILLIS = 1000;

    private final CommandQueue commandQueue = new ConcurrentCommandQueue(new CommandExceptionHandler());

    private final SingleThreadExecutor singleThreadExecutor = new SingleThreadExecutor(commandQueue);



    @Test
    void simpleCommandTest() {

        singleThreadExecutor.start();

        IntStream.range(0, 10).forEach(number -> {
            commandQueue.add(new NumberPrinting(number));
            try {
                Thread.sleep(SLEEP_TIME_IN_MILLIS);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });

        try {
            Thread.sleep(SLEEP_TIME_IN_MILLIS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        singleThreadExecutor.stop();
    }


    private record NumberPrinting(int number) implements Command {

        @Override
        public void execute() {
            System.out.println("Number: " + number);
        }
    }
}