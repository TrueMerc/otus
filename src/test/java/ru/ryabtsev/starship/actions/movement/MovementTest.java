package ru.ryabtsev.starship.actions.movement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.TestConstants;
import ru.ryabtsev.starship.objects.Starship;

class MovementTest {

    @Test
    void linearMovementTest() {
        final Movable object = new Starship(new Vector(12, 5), new Vector(-7, 3));
        new Movement(object).execute();

        assertEquals(5, object.getPosition().x(), TestConstants.DELTA);
        assertEquals(8, object.getPosition().y(), TestConstants.DELTA);
    }

    @Test
    void movementErrorsTest() {
        assertThrows(IllegalStateException.class, () -> {
            new Movement(new Starship(null , new Vector(1, 1))).execute();
        });
        assertThrows(IllegalStateException.class, () -> {
            new Movement(new Starship(new Vector(1, 1) , null)).execute();
        });
        assertThrows(IllegalStateException.class, () -> {
            final Movable object = new Starship(new Vector(1, 1), new Vector(1, 1));
            object.moveTo(null);
        });
    }
}