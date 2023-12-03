package ru.ryabtsev.starship.actions.fuel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.TestConstants;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.fuel.mocks.DummyFuelConsumer;

class FuelBurningTest {

    @Test
    void fuelBurningTest() {
        final FuelConsumer fuelConsumer = new DummyFuelConsumer(10);
        final Command fuelBurning = new FuelBurning(fuelConsumer, 5);
        fuelBurning.execute();
        assertEquals(5, fuelConsumer.getFuelLevel(), TestConstants.DELTA);
    }
}