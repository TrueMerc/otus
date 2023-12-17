package ru.ryabtsev.starship.actions.movement;

public record Vector(double x, double y) {

    double length() {
        return Math.sqrt(x * x + y * y);
    }
}
