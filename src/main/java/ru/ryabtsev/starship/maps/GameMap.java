package ru.ryabtsev.starship.maps;

import java.util.List;
import java.util.ArrayList;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.objects.properties.CollisionProne;

/**
 * The implementation of map that is used for game.
 */
public class GameMap {

    private static final double DEFAULT_REGION_SIZE = 16.0;

    private static final int DEFAULT_NUMBER_OF_OBJECTS = 1000;

    private final List<MapRegion> regions;

    private final List<Object> objects;

    private final int widthInRegions;

    private final int heightInRegions;

    private final double widthInUnits;

    private final double heightInUnits;

    public GameMap(int widthInRegions, int heightInRegions) {
        if (widthInRegions <= 0 ||heightInRegions <= 0) {
            throw new IllegalArgumentException("Map's width and height must be positive numbers");
        }

        regions = new ArrayList<>(widthInRegions * heightInRegions);
        for (int height = 0; height < heightInRegions; ++height) {
            for (int width = 0; width < widthInRegions; ++width) {
                final Vector upperLeftCorner = new Vector(DEFAULT_REGION_SIZE * width, height);
                final Vector lowerRightCorner = new Vector(
                        DEFAULT_REGION_SIZE * (width + 1), DEFAULT_REGION_SIZE * (height + 1)
                );
                regions.add(height * widthInRegions + width, new MapRegion(upperLeftCorner, lowerRightCorner));
            }
        }
        this.objects = new ArrayList<>(1000);
        this.widthInRegions = widthInRegions;
        this.heightInRegions = heightInRegions;
        widthInUnits = DEFAULT_REGION_SIZE * widthInRegions;
        heightInUnits = DEFAULT_REGION_SIZE * heightInRegions;
    }

    public MapRegion findRegionOf(final Vector point) {
        final Vector correctedCoordinates = new Vector(
                correctedValue(point.x(), widthInUnits),
                correctedValue(point.y(), heightInUnits)
        );
        final int width = Double.valueOf(Math.floor(correctedCoordinates.x() / DEFAULT_REGION_SIZE)).intValue();
        final int height = Double.valueOf(Math.floor(correctedCoordinates.y() / DEFAULT_REGION_SIZE)).intValue();
        return regions.get(height * widthInRegions + width);
    }

    private double correctedValue(double value, double maxValue) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > maxValue) {
            return widthInUnits;
        }
        return value;
    }



    public GameMap add(final Object object) {
        objects.add(object);
        if (object instanceof CollisionProne collisionProne) {
            final MapRegion region = findRegionOf(collisionProne.getPosition());
            region.add(collisionProne);
        }
        return this;
    }

    public List<MapRegion> getRegions() {
        return regions;
    }

    public <T> List<T> getAll(final Class<T> requiredClass) {
        return objects.stream()
                .filter(object -> requiredClass.isAssignableFrom(object.getClass()))
                .map(requiredClass::cast)
                .toList();
    }
}
