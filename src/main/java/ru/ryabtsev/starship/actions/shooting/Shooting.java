package ru.ryabtsev.starship.actions.shooting;

import ru.ryabtsev.starship.actions.Command;

public class Shooting implements Command {

    private final Shooter shooter;

    public Shooting(final Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public void execute() {
        shooter.shoot();
    }
}
