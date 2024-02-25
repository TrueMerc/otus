package ru.ryabtsev.starship.actions.interaction;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.interactions.Intersection;
import ru.ryabtsev.starship.maps.GameMap;
import ru.ryabtsev.starship.maps.MapRegion;
import ru.ryabtsev.starship.objects.properties.CollisionProne;

public class MultiregionalCollisionCheck implements Command {

    private final GameMap map;

    private final Movable object;

    private boolean isSuccessful;

    public MultiregionalCollisionCheck(GameMap map, Movable object) {
        this.map = map;
        this.object = object;
        this.isSuccessful = false;
    }

    @Override
    public void execute() {
        if (object instanceof CollisionProne collisionProne) {
            final CollisionCheck simpleCheck = new CollisionCheck(map, object);
            simpleCheck.execute();
            isSuccessful = simpleCheck.isSuccessful()
                    || checkAdditionalRegions(collisionProne);
        }
    }

    private boolean checkAdditionalRegions(final CollisionProne collisionProne) {
        final MapRegion mainRegion = map.findRegionOf(collisionProne.getPosition());
        final Vector mainRegionCenter = mainRegion.getCenter();
        final Vector objectCenter = collisionProne.getPosition();
        final double widthAxisSign = (objectCenter.x() - mainRegionCenter.x()) < 0 ? -1.0 : 1.0;
        final double heightAxisSign = (objectCenter.y() - mainRegionCenter.y()) < 0 ? -1.0 : 1.0;
        final Vector pointWithWidthShift = new Vector(
                mainRegionCenter.x() + widthAxisSign * mainRegion.getSize(),
                mainRegionCenter.y()
        );
        final Vector pointWithHeightShift = new Vector(
                mainRegionCenter.x(),
                mainRegionCenter.y() + heightAxisSign * mainRegion.getSize()
        );
        final Vector oppositeCorner = new Vector(
                mainRegionCenter.x() + widthAxisSign * mainRegion.getSize(),
                mainRegionCenter.y() + heightAxisSign * mainRegion.getSize()
        );

        final List<Vector> newRegionVertices = new java.util.ArrayList<>(List.of(
                mainRegionCenter, pointWithHeightShift, pointWithWidthShift, oppositeCorner
        ));

        newRegionVertices.sort((o1, o2) -> {
            if(o1.x() < o2.x() || o1.y() < o2.y()) {
                return -1;
            }
            if(o1.x() > o2.x() || o1.y() > o2.y()) {
                return 1;
            }
            return 0;
        });

        final MapRegion intermediateRegion = new MapRegion(newRegionVertices.get(0), newRegionVertices.get(3));
        final List<MapRegion> neighbors = List.of(
                map.findRegionOf(pointWithWidthShift),
                map.findRegionOf(pointWithHeightShift),
                map.findRegionOf(oppositeCorner)
        );

        neighbors.stream()
                .map(region -> region.getCollisionProneObjectsExcept(collisionProne))
                .flatMap(List::stream)
                .forEach(intermediateRegion::addIfContains);

        return intermediateRegion.getCollisionProneObjects().stream().anyMatch(
                collisionProneObject -> Intersection.of(collisionProneObject, collisionProne).isHappened()
        );
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
