package ru.ryabtsev.starship.interactions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.objects.properties.Circle;

class IntersectionOfCircleAndCircleTest {

    @Test
    void simpleIntersectionTest() {
        // Arrange:
        final Circle circleOne = new Circle(new Vector(1.0, 1.0), 1.0);
        final Circle circleTwo = new Circle(new Vector(1.0, 1.0), 1.0);

        // Act:
        final Intersection intersection = Intersection.of(circleOne, circleTwo);

        // Assert:
        assertTrue(intersection.isHappened());
    }

    @Test
    void tangentCirclesIntersectionTest() {
        // Arrange:
        final Circle circleOne = new Circle(new Vector(1.0, 1.0), 1.0);
        final Circle circleTwo = new Circle(new Vector(3.0, 1.0), 1.0);

        // Act:
        final Intersection intersection = Intersection.of(circleOne, circleTwo);

        // Assert:
        assertTrue(intersection.isHappened());
    }

    @Test
    void intersectionIsNotHappenedTest() {
        // Arrange:
        final Circle circleOne = new Circle(new Vector(1.0, 1.0), 1.0);
        final Circle circleTwo = new Circle(new Vector(3.1, 1.0), 1.0);

        // Act:
        final Intersection intersection = Intersection.of(circleOne, circleTwo);

        // Assert:
        assertFalse(intersection.isHappened());
    }

}