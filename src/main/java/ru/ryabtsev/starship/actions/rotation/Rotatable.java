package ru.ryabtsev.starship.actions.rotation;

public interface Rotatable {

    double getCourse();

    double getAngularVelocity();

    void changeCourse(double course);
}
