package ru.ryabtsev.starship.actions.rotation;

import ru.ryabtsev.starship.actions.Command;

public class Rotation implements Command {

    private final Rotatable rotatable;

    public Rotation(Rotatable rotatable) {
        this.rotatable = rotatable;
    }

    public void execute() {
        rotatable.changeCourse(rotatable.getCourse() + rotatable.getAngularVelocity());
    }
}
