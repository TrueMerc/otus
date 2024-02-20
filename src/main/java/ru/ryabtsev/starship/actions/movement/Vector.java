package ru.ryabtsev.starship.actions.movement;

public record Vector(double x, double y) {

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector minus(final Vector vector) {
        return new Vector(x - vector.x(), y - vector.y());
    }
}
