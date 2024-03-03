package ru.ryabtsev.starship.processors;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.context.ChildContextCreation;
import ru.ryabtsev.starship.actions.context.DependencyRegistration;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.actions.movement.Movement;
import ru.ryabtsev.starship.actions.movement.MovementStop;
import ru.ryabtsev.starship.actions.movement.ObjectWithChangeableVelocity;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.actions.movement.VelocityChange;
import ru.ryabtsev.starship.actions.shooting.Ammunition;
import ru.ryabtsev.starship.actions.shooting.Shooter;
import ru.ryabtsev.starship.actions.shooting.Shooting;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.context.ContextSelection;
import ru.ryabtsev.starship.context.SimpleApplicationContext;
import ru.ryabtsev.starship.exceptions.handlers.CommandExceptionHandler;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.executors.ConcurrentCommandQueue;

class ActionMessageProcessorTest {

    private static final String PLAYER_ONE_NAME = "PlayerOne";

    private static final String PLAYER_TWO_NAME = "PlayerTwo";

    private static final String CONTEXT_SUFFIX = "Context";

    private static final String DEPENDENCY_REGISTRATION = DependencyRegistration.class.getSimpleName();

    private static final String CHILD_CONTEXT_CREATION = ChildContextCreation.class.getSimpleName();

    private static final String CONTEXT_SELECTION = ContextSelection.class.getSimpleName();

    private static final String PLAYER_ONE_CONTEXT_NAME = PLAYER_ONE_NAME + CONTEXT_SUFFIX;

    private static final String PLAYER_TWO_CONTEXT_NAME = PLAYER_TWO_NAME + CONTEXT_SUFFIX;

    private static final String PLAYER_ONE_STARSHIP_ID = "PlayerOneSpaceshipId";

    private static final String PLAYER_TWO_STARSHIP_ID = "PlayerTwoSpaceshipId";

    private final CommandQueue commandQueue = new ConcurrentCommandQueue(new CommandExceptionHandler());

    private final ApplicationContext mainContext = new SimpleApplicationContext();

    private final ActionMessageProcessor actionMessageProcessor = new ActionMessageProcessor(mainContext, commandQueue);

    @BeforeEach
    void setUp() {
        final Function<Object[], Object> commandQueueProvider = objects -> commandQueue;
        mainContext.<Command>resolve(DEPENDENCY_REGISTRATION, "MessageQueue", commandQueueProvider).execute();
        createChildContext(PLAYER_ONE_CONTEXT_NAME);
        createChildContext(PLAYER_TWO_CONTEXT_NAME);
        registerContextSelectionCommand();
        registerApiMap();
    }

    private void createChildContext(final String contextName) {
        final Function<Object[], Object> contextCreation = objects -> new ChildContextCreation(
                mainContext, contextName
        );
        mainContext.<Command>resolve(DEPENDENCY_REGISTRATION, contextCreationName(contextName), contextCreation)
                .execute();
        mainContext.<Command>resolve(contextCreationName(contextName)).execute();
    }

    private String contextCreationName(final String contextName) {
        return contextName + CHILD_CONTEXT_CREATION;
    }

    private void registerContextSelectionCommand() {
        final Function<Object[], Object> contextSelection = objects -> new ContextSelection(mainContext)
                .getChild((String) objects[0]);
        mainContext.<Command>resolve(DEPENDENCY_REGISTRATION, CONTEXT_SELECTION, contextSelection).execute();
    }

    private void registerApiMap() {
        final Map<String, String> apiMap = Map.of(
                "movement", Movement.class.getName(),
                "movementStop", MovementStop.class.getName(),
                "velocityChange", VelocityChange.class.getName(),
                "shooting", Shooting.class.getName()
        );
        final Function<Object[], Object> apiMapProvider = objects -> apiMap;
        mainContext.<Command>resolve(DEPENDENCY_REGISTRATION, "ApiMap", apiMapProvider).execute();
    }


    @Test
    void differentPlayerObjectsRegistrationTest() {
        final var starships = createStarships();
        final var playerOneStarship = starships.get(0);
        final var playerTwoStarship = starships.get(1);

        final ApplicationContext playerOneContext = mainContext.resolve(CONTEXT_SELECTION, PLAYER_ONE_CONTEXT_NAME);
        final SimpleStarship someStarship = playerOneContext.resolve(PLAYER_ONE_STARSHIP_ID);
        final ApplicationContext playerTwoContext = mainContext.resolve(CONTEXT_SELECTION, PLAYER_TWO_CONTEXT_NAME);
        final SimpleStarship anotherStarship = playerTwoContext.resolve(PLAYER_TWO_STARSHIP_ID);

        assertEquals(playerOneStarship, someStarship);
        assertEquals(playerTwoStarship, anotherStarship);
    }

    private List<SimpleStarship> createStarships() {
        final var defaultVelocity = new Vector(1.0, 1.0);
        final SimpleStarship playerOneStarship = new SimpleStarship(new Vector(0.0, 0.0), defaultVelocity);
        final SimpleStarship playerTwoStarship = new SimpleStarship(new Vector(3.0, 3.0), defaultVelocity);

        registerStarship(PLAYER_ONE_CONTEXT_NAME, PLAYER_ONE_STARSHIP_ID, playerOneStarship);
        registerStarship(PLAYER_TWO_CONTEXT_NAME, PLAYER_TWO_STARSHIP_ID, playerTwoStarship);

        return List.of(playerOneStarship, playerTwoStarship);
    }

    @Test
    void orderExecutionTest() {
        final var starships = createStarships();
        final var playerOneStarship = starships.get(0);
        final var playerTwoStarship = starships.get(1);

        // language=JSON
        final String playerOneMessage = """
                {
                    "game": "gameId",
                    "object": "%s",
                    "action": "velocityChange",
                    "parameters": [2.0, 2.0]
                }""".formatted(PLAYER_ONE_STARSHIP_ID);

        // language=JSON
        final String playerTwoMessage = """
                {
                    "game": "gameId",
                    "object": "%s",
                    "action": "movementStop"
                }""".formatted(PLAYER_TWO_STARSHIP_ID);

        // Act:
        actionMessageProcessor.process(PLAYER_ONE_NAME, playerOneMessage);
        actionMessageProcessor.process(PLAYER_TWO_NAME, playerTwoMessage);

        while (!commandQueue.isEmpty()) {
            commandQueue.execute();
        }

        // Assert:
        assertEquals(new Vector(2.0, 2.0), playerOneStarship.getVelocity());
        assertEquals(new Vector(0.0, 0.0), playerTwoStarship.getVelocity());
    }

    private void registerStarship(final String contextName, final String id, final SimpleStarship starship) {
        final ApplicationContext playerContext = mainContext.resolve(
                CONTEXT_SELECTION, new Object[]{ contextName });
        final Function<Object[], Object> starshipProvider = (objects) -> starship;
        playerContext.<Command>resolve(DEPENDENCY_REGISTRATION, id, starshipProvider).execute();
    }

    @Slf4j
    private static class SimpleStarship implements Movable, ObjectWithChangeableVelocity, Shooter {

        private Vector position;

        private Vector velocity;

        SimpleStarship(Vector position, Vector velocity) {
            this.position = position;
            this.velocity = velocity;
        }

        @Override
        public Vector getPosition() {
            return position;
        }

        @Override
        public Vector getVelocity() {
            return velocity;
        }

        @Override
        public void moveTo(Vector position) {
            this.position = position;
        }

        @Override
        public void finish() {

        }

        @Override
        public void changeVelocity(Vector velocity) {
            this.velocity = velocity;
        }

        @Override
        public Ammunition shoot() {
            final var missile = new Missile();
            log.info("Spaceship {} has shot missile {}", this, missile);
            return new Missile();
        }
    }

    private static class Missile implements Ammunition {

        @Override
        public Vector getPosition() {
            return null;
        }

        @Override
        public Vector getVelocity() {
            return null;
        }

        @Override
        public void moveTo(Vector position) {

        }

        @Override
        public void finish() {

        }

        @Override
        public int damageRate() {
            return 1000;
        }
    }
}