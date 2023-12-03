package ru.ryabtsev.starship.actions.fuel;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.exceptions.LowFuelLevelException;

/**
 * The class that is implementing command associated with fuel check.
 */
public class FuelCheck implements Command {

    private static final String LOW_FUEL_LEVEL_MESSAGE = "Fuel level is to low %.3f, %.3f amount of fuel required";

    private final FuelConsumer consumer;

    private final double requiredLevel;

    public FuelCheck(FuelConsumer consumer, double requiredLevel) {
        this.consumer = consumer;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public void execute() {
        if (Double.compare(requiredLevel, consumer.getFuelLevel()) > 0) {
            final String message = String.format(LOW_FUEL_LEVEL_MESSAGE, consumer.getFuelLevel(), requiredLevel);
            throw new LowFuelLevelException(message);
        }
    }
}
