package ru.ryabtsev.starship.actions.fuel;

import ru.ryabtsev.starship.actions.Command;

public class FuelBurning implements Command {

    private final FuelConsumer fuelConsumer;

    private final double amount;

    public FuelBurning(final FuelConsumer fuelConsumer, final double amount) {
        this.fuelConsumer = fuelConsumer;
        this.amount = amount;
    }

    @Override
    public void execute() {
        fuelConsumer.consume(amount);
    }
}
