package ru.ryabtsev.starship.actions.interaction;

import java.util.List;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.interactions.Intersection;
import ru.ryabtsev.starship.maps.GameMap;
import ru.ryabtsev.starship.maps.MapRegion;
import ru.ryabtsev.starship.objects.properties.CollisionProne;

public class CollisionCheck implements Command {


    private final GameMap map;

    private final Movable object;

    private boolean isSuccessful;

    public CollisionCheck(final GameMap map, final Movable object) {
        this.map = map;
        this.object = object;
    }

    @Override
    public void execute() {
        if (object instanceof CollisionProne collisionProne) {
            final MapRegion region = map.findRegionOf(object.getPosition());
            final List<CollisionProne> collisionProneObjects = region.getCollisionProneObjectsExcept(collisionProne);
            isSuccessful = collisionProneObjects.stream()
                    .anyMatch(object -> Intersection.of(object, collisionProne).isHappened());
        }
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
