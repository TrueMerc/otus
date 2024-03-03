package ru.ryabtsev.starship.actions.movement;

import ru.ryabtsev.starship.actions.Command;

public class VelocityChange implements Command {

    private final ObjectWithChangeableVelocity object;

    private final Vector newVelocity;

    public VelocityChange(ObjectWithChangeableVelocity object, Vector newVelocity) {
        this.object = object;
        this.newVelocity = newVelocity;
    }

    @Override
    public void execute() {
        object.changeVelocity(newVelocity);
    }
}
