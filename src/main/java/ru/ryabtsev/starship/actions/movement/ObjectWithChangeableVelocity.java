package ru.ryabtsev.starship.actions.movement;

/**
 * The interface for all objects that have velocity which can be changed.
 */
public interface ObjectWithChangeableVelocity {

    public void changeVelocity(Vector velocity);
}
