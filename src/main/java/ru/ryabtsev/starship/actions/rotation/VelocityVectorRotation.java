package ru.ryabtsev.starship.actions.rotation;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.actions.movement.ObjectWithChangeableVelocity;
import ru.ryabtsev.starship.actions.movement.Vector;

/**
 * The MovementVectorRotation class implements rotation of a velocity vector if a target object has it, and it can
 * be changed.
 */
public class VelocityVectorRotation implements Command {

    private final Rotatable target;

    public VelocityVectorRotation(final Rotatable target) {
        this.target = target;
    }

    @Override
    public void execute() {
        if (target instanceof Movable movableTarget
                && target instanceof ObjectWithChangeableVelocity objectWithChangeableVelocity) {
            final Vector v = movableTarget.getVelocity();
            final double angle = target.getXAxisAdjustedCourse();
            final double cos = StrictMath.cos(angle);
            final double sin = StrictMath.sin(angle);
            final Vector newVelocity = new Vector(v.x() * cos - v.y() * sin, v.x() * sin + v.y() * cos);
            objectWithChangeableVelocity.changeVelocity(newVelocity);
        }
    }
}
