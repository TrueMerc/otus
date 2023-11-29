package ru.ryabtsev.starship.actions.rotation;

public class Rotation {

    private final Rotatable rotatable;

    public Rotation(Rotatable rotatable) {
        this.rotatable = rotatable;
    }

    public void execute() {
        rotatable.changeCourse(rotatable.getCourse() + rotatable.getAngularVelocity());
    }
}
