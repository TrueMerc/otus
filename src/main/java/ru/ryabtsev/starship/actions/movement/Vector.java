package ru.ryabtsev.starship.actions.movement;

import java.util.Objects;

public record Vector(double x, double y) {

    private static final double DOUBLE_PRECISION = 1.0e-12;

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector minus(final Vector vector) {
        return new Vector(x - vector.x(), y - vector.y());
    }

    public Vector plus(final Vector vector) {
        return new Vector(x + vector.x(), y + vector.y());
    }

    public  Vector multiply(final double scalar) {
        return new Vector(scalar * x, scalar * y);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Vector vector = (Vector) o;
        return Math.abs(vector.x - x) < DOUBLE_PRECISION && Math.abs(vector.y - y) < DOUBLE_PRECISION;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
