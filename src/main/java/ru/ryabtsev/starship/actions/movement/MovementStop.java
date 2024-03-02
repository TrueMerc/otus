package ru.ryabtsev.starship.actions.movement;

public class MovementStop extends SpeedChange {

    private static final Vector ZERO_VELOCITY = new Vector(0, 0);

    public MovementStop(final ObjectWithChangeableVelocity object) {
        super(object, ZERO_VELOCITY);
    }
}
