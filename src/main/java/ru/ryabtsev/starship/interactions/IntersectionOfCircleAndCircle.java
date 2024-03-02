package ru.ryabtsev.starship.interactions;

import ru.ryabtsev.starship.objects.properties.Circle;

public record IntersectionOfCircleAndCircle(Circle firstCircle, Circle secondCircle) implements Intersection {

    @Override
    public boolean isHappened() {
        final double distanceBetweenCenters = firstCircle.getCenter().minus(secondCircle.getCenter()).length();
        final double sumOfRadii = firstCircle.getRadius() + secondCircle().getRadius();
        return distanceBetweenCenters <= sumOfRadii;
    }
}
