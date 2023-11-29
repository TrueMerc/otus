package ru.ryabtsev.starship.actions.rotation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.TestConstants;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.objects.Starship;

class RotationTest {

    @Test
    void linearRotationTest() {
        final Rotatable rotatable = Starship.builder()
                .withPosition(new Vector(1, 1))
                .withVelocity(new Vector(1, 1))
                .withCourse(0)
                .withAngularVelocity(5)
                .build();

        new Rotation(rotatable).execute();
        assertEquals(5, rotatable.getCourse(), TestConstants.DELTA);
    }

}