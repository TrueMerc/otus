package ru.ryabtsev.starship.interactions;

import ru.ryabtsev.starship.objects.properties.Circle;
import ru.ryabtsev.starship.objects.properties.Shape;

public interface Intersection {

    boolean isHappened();

    static Intersection of(final Shape firstShape, final Shape secondShape) {
        if (firstShape instanceof Circle firstCircle && secondShape instanceof Circle secondCircle) {
            return new IntersectionOfCircleAndCircle(firstCircle, secondCircle);
        } else {
            throw new IllegalArgumentException("Current version of application supports only objects with round shape");
        }
    }

}
