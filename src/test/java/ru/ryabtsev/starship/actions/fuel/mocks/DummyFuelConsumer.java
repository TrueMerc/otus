package ru.ryabtsev.starship.actions.fuel.mocks;

import ru.ryabtsev.starship.actions.fuel.FuelConsumer;

public class DummyFuelConsumer implements FuelConsumer {

    private double fuelLevel;

    public DummyFuelConsumer(final double fuelLevel) {
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
