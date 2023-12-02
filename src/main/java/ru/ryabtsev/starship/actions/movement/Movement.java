package ru.ryabtsev.starship.actions.movement;

import java.util.Objects;
import ru.ryabtsev.starship.actions.Command;

public class Movement implements Command {

    private final Movable object;

    public Movement(final Movable object) {
        this.object = object;
    }

    public void execute() {
        try {
            final Vector position = Objects.requireNonNull(object.getPosition());
            final Vector velocity = Objects.requireNonNull(object.getVelocity());
            object.moveTo(new Vector(position.x() + velocity.x(), position.y() + velocity.y()));
        } catch (RuntimeException e) {
            throw new IllegalStateException("Can't perform movement operation", e);
        }
    }
}
