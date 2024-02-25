package ru.ryabtsev.starship.actions.movement;

import java.util.Objects;

public record Vector(double x, double y) {

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector minus(final Vector vector) {
        return new Vector(x - vector.x(), y - vector.y());
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
        return Double.compare(vector.x, x) == 0 && Double.compare(vector.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
