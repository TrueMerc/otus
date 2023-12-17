package ru.ryabtsev.starship.actions.rotation;

public interface Rotatable {

    /**
     * Returns object's current course. 0 value means that an object is directed on the North Pole (its direction
     * is collinear with the Y axis).
     * @return object's current course in radians.
     */
    double getCourse();

    /**
     * Returns object's angular velocity.
     * @return object's angular velocity in radians.
     */
    double getAngularVelocity();

    void changeCourse(double course);

    /**
     * Returns object's course adjusted to the X axis based coordinate system. This method was added for convenience
     * of calculations (for example, vector rotations).
     * @return object's course adjusted to the X axis based coordinate system in radians.
     */
    default double getXAxisAdjustedCourse() {
        return Math.PI / 2 - getCourse();
    }
}
