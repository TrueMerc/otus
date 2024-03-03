package ru.ryabtsev.starship.actions.shooting;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.maps.GameMap;

public class Shooting implements Command {

    private final Shooter shooter;

    public Shooting(final Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public void execute() {
        final Ammunition ammunition = shooter.shoot();
    }
}
