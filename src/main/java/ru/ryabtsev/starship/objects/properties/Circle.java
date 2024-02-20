package ru.ryabtsev.starship.objects.properties;

import ru.ryabtsev.starship.actions.movement.Vector;

public record Circle(Vector center, double radius) implements Shape {
}
