package ru.ryabtsev.starship.actions.fuel;

/**
 * The base class for all game objects that consumes fuel to produce some actions.
 */
public interface FuelConsumer {

    /**
     * Adds given amount of fuel.
     * @param amount amount of fuel that has to be added into fuel tank.
     */
    void fuel(final double amount);

    /**
     * Consumes given amount of fuel to produce some action.
     * @param amount amount of fuel that has to be consumed.
     */
    void consume(final double amount);

    /**
     * Returns current amount of fuel in the fuel tank.
     */
    double getFuelLevel();
}
