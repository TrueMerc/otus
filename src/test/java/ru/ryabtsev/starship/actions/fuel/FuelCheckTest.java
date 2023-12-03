package ru.ryabtsev.starship.actions.fuel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.exceptions.LowFuelLevelException;

class FuelCheckTest {

    private final FuelConsumer fuelConsumer = new DummyFuelConsumer(10);
    @Test
    void successfulFuelCheckExecutionTest() {
        final Command fuelCheck = new FuelCheck(fuelConsumer, 5);
        assertDoesNotThrow(fuelCheck::execute);
    }

    @Test
    void unsuccessfulFuelCheckExecutionTest() {
        final Command fuelCheck = new FuelCheck(fuelConsumer, 100);
        assertThrows(LowFuelLevelException.class, fuelCheck::execute);
    }

    private static class DummyFuelConsumer implements FuelConsumer {

        private double fuelLevel;

        DummyFuelConsumer(final double fuelLevel) {
            this.fuelLevel = fuelLevel;
        }

        @Override
        public void fuel(final double amount) {
            fuelLevel += amount;
        }

        @Override
        public void consume(final double amount) {
            fuelLevel -= amount;
        }

        @Override
        public double getFuelLevel() {
            return fuelLevel;
        }
    }
}