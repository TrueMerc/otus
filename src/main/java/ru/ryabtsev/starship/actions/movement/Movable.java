package ru.ryabtsev.starship.actions.movement;

public interface Movable {

    Vector getPosition();

    Vector getVelocity();

    void moveTo(Vector position);
}
