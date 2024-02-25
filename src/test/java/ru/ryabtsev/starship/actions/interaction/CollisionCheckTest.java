package ru.ryabtsev.starship.actions.interaction;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.CompositeCommand;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.actions.movement.MovementOnMap;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.maps.GameMap;
import ru.ryabtsev.starship.objects.properties.Circle;
import ru.ryabtsev.starship.objects.properties.CollisionProne;

class CollisionCheckTest {

    @Test
    void simpleCollisionTest() {
        // Arrange:
        final GameMap map = new GameMap(2, 2);
        final CollisionProne starshipOne = new SimpleStarship(new Vector(1, 1), new Vector(16, 0), 2);
        final CollisionProne starshipTwo = new SimpleStarship(new Vector(18, 1), new Vector(0, 0), 1);
        map.add(starshipOne).add(starshipTwo);
        final Command movementOnMap = new MovementOnMap(map, starshipOne);
        final CollisionCheck collisionCheck = new CollisionCheck(map, starshipOne);
        final Command movementWithCollisionCheck = new CompositeCommand(movementOnMap, collisionCheck);

        // Act:
        movementWithCollisionCheck.execute();

        // Assert:
        assertTrue(collisionCheck.isSuccessful());
    }


    private static class SimpleStarship extends Circle implements CollisionProne {

        private Vector velocity;

        public SimpleStarship(final Vector position, final Vector velocity, final double radius) {
            super(position, radius);
            this.velocity = velocity;
        }

        @Override
        public Vector getPosition() {
            return getCenter();
        }

        @Override
        public Vector getVelocity() {
            return velocity;
        }

        @Override
        public void moveTo(final Vector position) {
            super.setCenter(position);
        }

        @Override
        public void finish() {
            throw new UnsupportedOperationException("This operation was added only for testing purpose");
        }
    }
}