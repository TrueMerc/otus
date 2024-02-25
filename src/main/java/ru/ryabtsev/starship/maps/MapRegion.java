package ru.ryabtsev.starship.maps;

import java.util.ArrayList;
import java.util.List;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.objects.properties.CollisionProne;

/**
 * The class that defines a square part of the whole map and serves to limit some calculations.
 * The whole map is separated into these regions.
 */
public class MapRegion {

    private static final int DEFAULT_NUMBER_OF_OBJECTS = 100;

    private final List<CollisionProne> objects;

    private final Vector upperLeftCorner;

    private final Vector lowerRightCorner;

    private final double size;

    public MapRegion(final Vector upperLeftCorner, final Vector lowerRightCorner) {
        objects = new ArrayList<>(DEFAULT_NUMBER_OF_OBJECTS);
        this.upperLeftCorner = upperLeftCorner;
        this.lowerRightCorner = lowerRightCorner;
        size = lowerRightCorner.x() - upperLeftCorner.x();
    }

    public void add(final CollisionProne object) {
        objects.add(object);
    }

    public void remove(final CollisionProne object) {
        objects.remove(object);
    }
}
