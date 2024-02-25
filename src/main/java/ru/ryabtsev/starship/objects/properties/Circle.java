package ru.ryabtsev.starship.objects.properties;

import java.util.Objects;
import ru.ryabtsev.starship.actions.movement.Vector;

public class Circle implements Shape {
    private Vector center;
    private final double radius;

    public Circle(Vector center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vector getCenter() {
        return center;
    }

    public void setCenter(final Vector center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Circle) obj;
        return Objects.equals(this.center, that.center) &&
                Double.doubleToLongBits(this.radius) == Double.doubleToLongBits(that.radius);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, radius);
    }

    @Override
    public String toString() {
        return "Circle[" +
                "center=" + center + ", " +
                "radius=" + radius + ']';
    }
}
