package ru.ryabtsev.starship.exceptions.handlers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.logging.ExceptionLog;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.actions.movement.Movement;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.actions.repetition.Repeat;
import ru.ryabtsev.starship.objects.Starship;

class CommandExceptionHandlerTest {

    @Test
    void commandExecutionHandlerSimpleTest() {
        final CommandExceptionHandler commandExceptionHandler = new CommandExceptionHandler();
        final BiFunction<Command, Exception, Command> firstHandler = (command, exception) -> new Repeat(command);
        commandExceptionHandler.register(Movement.class, IllegalStateException.class, firstHandler);
        final BiFunction<Command, Exception, Command> secondHandler =
                (command, exception) -> new ExceptionLog(exception);
        commandExceptionHandler.register(Repeat.class, IllegalStateException.class, secondHandler);

        final Optional<Command> firstResult = commandExceptionHandler.handle(
                new Movement(new DummyMovable()), new IllegalStateException("Cause"));

        assertTrue(firstResult.isPresent());
        firstResult.ifPresent(result -> assertEquals(Repeat.class, result.getClass()));

        final Optional<Command> secondResult = commandExceptionHandler.handle(
                new Repeat(null), new IllegalStateException("Cause"));
        assertTrue(secondResult.isPresent());
        secondResult.ifPresent(result -> assertEquals(ExceptionLog.class, result.getClass()));
    }

    private static class DummyMovable implements Movable {
        @Override
        public Vector getPosition() {
            return null;
        }

        @Override
        public Vector getVelocity() {
            return null;
        }

        @Override
        public void moveTo(Vector position) {

        }

        @Override
        public void finish() {

        }
    }
}