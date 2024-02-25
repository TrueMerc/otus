package ru.ryabtsev.starship.actions.movement;

import java.util.Objects;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.maps.GameMap;
import ru.ryabtsev.starship.maps.MapRegion;
import ru.ryabtsev.starship.objects.properties.CollisionProne;

public class MovementOnMap implements Command {

    private final GameMap map;

    private final Movable object;

    public MovementOnMap(final GameMap map, final Movable object) {
        this.map = map;
        this.object = object;
    }

    @Override
    public void execute() {
        final Command movement = new Movement(object);
        if (object instanceof CollisionProne collisionProne) {
            final Vector oldPosition = object.getPosition();
            final MapRegion oldRegion = map.findRegionOf(oldPosition);
            movement.execute();
            final Vector newPosition = object.getPosition();
            final MapRegion newRegion = map.findRegionOf(newPosition);
            if (!Objects.equals(oldRegion, newRegion)) {
                oldRegion.remove(collisionProne);
                newRegion.add(collisionProne);
            }
        } else {
            movement.execute();
        }
    }
}
