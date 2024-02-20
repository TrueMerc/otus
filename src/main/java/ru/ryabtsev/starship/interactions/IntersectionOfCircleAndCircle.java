package ru.ryabtsev.starship.interactions;

import ru.ryabtsev.starship.objects.properties.Circle;

public record IntersectionOfCircleAndCircle(Circle firstCircle, Circle secondCircle) implements Intersection {

    @Override
    public boolean isHappened() {
        final double distanceBetweenCenters = firstCircle.center().minus(secondCircle.center()).length();
        final double sumOfRadii = firstCircle.radius() + secondCircle().radius();
        return distanceBetweenCenters <= sumOfRadii;
    }
}
