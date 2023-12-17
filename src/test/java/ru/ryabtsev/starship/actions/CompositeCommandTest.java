package ru.ryabtsev.starship.actions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.TestConstants;
import ru.ryabtsev.starship.actions.fuel.FuelBurning;
import ru.ryabtsev.starship.actions.fuel.FuelCheck;
import ru.ryabtsev.starship.actions.movement.Movement;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.actions.rotation.Rotation;
import ru.ryabtsev.starship.actions.rotation.VelocityVectorRotation;
import ru.ryabtsev.starship.objects.Starship;

class CompositeCommandTest {

    private static final double FUEL_FOR_MOVEMENT = 1;

    private static final double FUEL_FOR_ROTATION = 1;

    private static final double INITIAL_FUEL_LEVEL = 10;

    private final Starship starship = Starship.builder()
            .withPosition(new Vector(0, 0))
            .withVelocity(new Vector(1, 0))
            .withAngularVelocity(Math.PI / 4)
            .withFuelLevel(INITIAL_FUEL_LEVEL)
            .build();

    @Test
    void linearMovementTest() {
        final Command fuelCheck = new FuelCheck(starship, FUEL_FOR_MOVEMENT);
        final Command movement = new Movement(starship);
        final Command fuelBurning = new FuelBurning(starship, FUEL_FOR_MOVEMENT);
        final Command linearMovementWithFuelConsumption = new CompositeCommand(fuelCheck, movement, fuelBurning);
        linearMovementWithFuelConsumption.execute();
        assertEquals(starship.getVelocity(), starship.getPosition());
        assertEquals(INITIAL_FUEL_LEVEL - FUEL_FOR_MOVEMENT, starship.getFuelLevel(), TestConstants.DELTA);

    }

    @Test
    void linearMovementWithRotationTest() {
        final Command fuelCheck = new FuelCheck(starship, FUEL_FOR_MOVEMENT);
        final Command movement = new Movement(starship);
        final Command fuelBurning = new FuelBurning(starship, FUEL_FOR_MOVEMENT);
        final Command rotation = new Rotation(starship);
        final Command velocityVectorRotation = new VelocityVectorRotation(starship);
        final Command linearMovementWithFuelConsumption = new CompositeCommand(
                fuelCheck, movement, fuelBurning, fuelCheck, rotation, velocityVectorRotation, fuelBurning);
        
        final Vector expectedPosition = starship.getVelocity();
        final double expectedCourse = starship.getCourse() + starship.getAngularVelocity();

        linearMovementWithFuelConsumption.execute();

        assertEquals(expectedPosition, starship.getPosition());
        assertEquals(1 / Math.sqrt(2), starship.getVelocity().x(), TestConstants.DELTA);
        assertEquals(1 / Math.sqrt(2), starship.getVelocity().y(), TestConstants.DELTA);
        final double expected = INITIAL_FUEL_LEVEL - FUEL_FOR_MOVEMENT - FUEL_FOR_ROTATION;
        assertEquals(expected, starship.getFuelLevel(), TestConstants.DELTA);
        assertEquals(expectedCourse, starship.getCourse(), TestConstants.DELTA);
    }
}