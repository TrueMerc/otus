package ru.ryabtsev.starship.actions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.TestConstants;
import ru.ryabtsev.starship.actions.fuel.FuelBurning;
import ru.ryabtsev.starship.actions.fuel.FuelCheck;
import ru.ryabtsev.starship.actions.movement.Movement;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.objects.Starship;

class CompositeCommandTest {

    private final Starship starship = Starship.builder()
            .withPosition(new Vector(0, 0))
            .withVelocity(new Vector(1, 0))
            .withAngularVelocity(0)
            .withFuelLevel(10)
            .build();

    @Test
    void linearMovementTest() {
        final double fuelForMovement = 1;
        final Command fuelCheck = new FuelCheck(starship, fuelForMovement);
        final Command movement = new Movement(starship);
        final Command fuelBurning = new FuelBurning(starship, fuelForMovement);
        final Command linearMovementWithFuelConsumption = new CompositeCommand(fuelCheck, movement, fuelBurning);
        linearMovementWithFuelConsumption.execute();
        assertEquals(starship.getVelocity(), starship.getPosition());
        assertEquals(10 - fuelForMovement, starship.getFuelLevel(), TestConstants.DELTA);
    }
}