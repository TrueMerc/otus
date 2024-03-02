package ru.ryabtsev.starship.actions.movement;

import ru.ryabtsev.starship.actions.Command;

public class SpeedChange implements Command {

    private final ObjectWithChangeableVelocity object;

    private final Vector newVelocity;

    public SpeedChange(ObjectWithChangeableVelocity object, Vector newVelocity) {
        this.object = object;
        this.newVelocity = newVelocity;
    }

    @Override
    public void execute() {
        object.changeVelocity(newVelocity);
    }
}
