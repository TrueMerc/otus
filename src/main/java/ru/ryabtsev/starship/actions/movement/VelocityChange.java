package ru.ryabtsev.starship.actions.movement;

import ru.ryabtsev.starship.actions.Command;

public class VelocityChange implements Command {

    private final ObjectWithChangeableVelocity object;

    private final Vector newVelocity;

    public VelocityChange(final ObjectWithChangeableVelocity object, final Vector newVelocity) {
        this.object = object;
        this.newVelocity = newVelocity;
    }

    public VelocityChange(ObjectWithChangeableVelocity object, Double xProjection, Double yProjection) {
        this(object, new Vector(xProjection, yProjection));
    }

    @Override
    public void execute() {
        object.changeVelocity(newVelocity);
    }
}
