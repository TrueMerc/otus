package ru.ryabtsev.starship.objects;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.actions.rotation.Rotatable;

@Getter
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Starship implements Movable, Rotatable {

    private Vector position;

    private final Vector velocity;

    private double course;

    private double angularVelocity;

    public Starship(final Vector position, final Vector velocity) {
        this.position = position;
        this.velocity = velocity;
        course = 0;
        angularVelocity = 0;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    @Override
    public void moveTo(final Vector position) {
        try {
            this.position = Objects.requireNonNull(position);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Can't move object to undefined position", e);
        }
    }

    @Override
    public double getCourse() {
        return course;
    }

    @Override
    public double getAngularVelocity() {
        return angularVelocity;
    }

    @Override
    public void changeCourse(final double course) {
        this.course = course;
    }
}
