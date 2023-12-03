package ru.ryabtsev.starship.executors;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.logging.ExceptionLog;
import ru.ryabtsev.starship.actions.movement.Movement;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.actions.repetition.Repeat;
import ru.ryabtsev.starship.actions.repetition.SecondTimeRepeat;
import ru.ryabtsev.starship.exceptions.handlers.CommandExceptionHandler;
import ru.ryabtsev.starship.objects.Starship;

class ConcurrentCommandQueueTest {

    @Test
    void oneTimeRepetitionAndLoggingTest() {
        final CommandExceptionHandler commandExceptionHandler = new CommandExceptionHandler();
        commandExceptionHandler.register(
                Movement.class,
                IllegalStateException.class,
                (movement, exception) -> new Repeat(movement)
        ).register(
                Repeat.class,
                IllegalStateException.class,
                (repeat, exception) -> new ExceptionLog(exception)
        );

        final var commandQueue = new ConcurrentCommandQueue(commandExceptionHandler);
        final var wrongPositionedStarship = new Starship(null, new Vector(0, 0));
        final var movement = new Movement(wrongPositionedStarship);

        commandQueue.add(movement);
        commandQueue.execute();
        assertNotNull(commandQueue.peek());
        assertEquals(Repeat.class, commandQueue.peek().getClass());

        commandQueue.execute();
        assertNotNull(commandQueue.peek());
        assertEquals(ExceptionLog.class, commandQueue.peek().getClass());
    }

    @Test
    void twoTimeRepetitionAndLoggingTest() {
        final CommandExceptionHandler commandExceptionHandler = new CommandExceptionHandler();
        commandExceptionHandler.register(
                Movement.class,
                IllegalStateException.class,
                (movement, exception) -> new Repeat(movement)
        ).register(
                Repeat.class,
                IllegalStateException.class,
                (repeat, exception) -> new SecondTimeRepeat(repeat)
        ).register(
                SecondTimeRepeat.class,
                IllegalStateException.class,
                (repeat, exception) -> new ExceptionLog(exception)
        );

        final var commandQueue = new ConcurrentCommandQueue(commandExceptionHandler);
        final var wrongPositionedStarship = new Starship(null, new Vector(0, 0));
        final var movement = new Movement(wrongPositionedStarship);

        commandQueue.add(movement);
        commandQueue.execute();
        assertNotNull(commandQueue.peek());
        assertEquals(Repeat.class, commandQueue.peek().getClass());

        commandQueue.execute();
        assertNotNull(commandQueue.peek());
        assertEquals(SecondTimeRepeat.class, commandQueue.peek().getClass());

        commandQueue.execute();
        assertNotNull(commandQueue.peek());
        assertEquals(ExceptionLog.class, commandQueue.peek().getClass());
    }

}