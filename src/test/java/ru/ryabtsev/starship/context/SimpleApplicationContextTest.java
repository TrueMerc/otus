package ru.ryabtsev.starship.context;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.context.ChildContextCreation;

class SimpleApplicationContextTest {

    private static final String REGISTRATION_COMMAND = "DependencyRegistration";

    private static final String ONE = "one";

    private static final String CHILD = "child";

    private static final String CHILD_CONTEXT_CREATION = ChildContextCreation.class.getSimpleName();

    private static final String CONTEXT_SELECTION = ContextSelection.class.getSimpleName();

    @Test
    void contextCreationTest() {
        final ApplicationContext context = new SimpleApplicationContext();
        final Function<Object[], Object> function = (objects) -> Long.valueOf(1);
        assertNotNull(context.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ ONE, function}));
    }

    @Test
    void dependencyRegistrationTest() {
        final ApplicationContext context = new SimpleApplicationContext();
        final Function<Object[], Object> function = (objects) -> Long.valueOf(1);
        context.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ ONE, function }).execute();
        assertEquals(1L, context.<Long>resolve(ONE, null).longValue());
    }

    @Test
    void childContextCreationTest() {
        final ApplicationContext context = new SimpleApplicationContext();
        final Function<Object[], Object> contextCreation = (objects) -> new ChildContextCreation(context, CHILD);
        context.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ CHILD_CONTEXT_CREATION, contextCreation})
                .execute();
        context.<Command>resolve(CHILD_CONTEXT_CREATION, null).execute();
        if (context instanceof SimpleApplicationContext typedContext) {
            assertEquals(1, typedContext.getChildren().size());
            assertTrue(typedContext.getChildren().get(0) instanceof SimpleApplicationContext);
        }
    }

    @Test
    void childContextSelectionTest() {
        final ApplicationContext context = new SimpleApplicationContext();
        final Function<Object[], Object> contextCreation = (objects) -> new ChildContextCreation(context, CHILD);
        context.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ CHILD_CONTEXT_CREATION, contextCreation})
                .execute();
        context.<Command>resolve(CHILD_CONTEXT_CREATION, null).execute();
        final Function<Object[], Object> contextSelection = (objects) ->
                new ContextSelection(context).getChild((String) objects[0]);
        context.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ CONTEXT_SELECTION, contextSelection}).execute();
        final ApplicationContext childContext = context.resolve(CONTEXT_SELECTION, new Object[]{ CHILD });
        final Function<Object[], Object> function = (objects) -> Long.valueOf(1);

        assertNotNull(childContext);
        assertNull(context.resolve(CONTEXT_SELECTION, new Object[]{ "IncorrectChild" }));

        childContext.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ ONE, function }).execute();

        assertEquals(1L, childContext.<Long>resolve(ONE, null).longValue());
        assertThrows(NullPointerException.class, () -> context.<Long>resolve(ONE, null).longValue());
    }

    @Test
    @SneakyThrows
    void multiThreadRunTest() {
        final ApplicationContext context = new SimpleApplicationContext();

        final Thread firstThread = new Thread(new ContextDependentTest(context, "FirstChild"));
        final Thread secondThread = new Thread(new ContextDependentTest(context, "SecondChild"));

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        final ApplicationContext firstContext = context.resolve(CONTEXT_SELECTION, new Object[]{ "FirstChild"});
        final ApplicationContext secondContext = context.resolve(CONTEXT_SELECTION, new Object[]{ "SecondChild"});

        assertNotNull(firstContext);
        assertNotNull(secondContext);

        assertEquals(1L, firstContext.<Long>resolve(ONE, null).longValue());
        assertEquals(1L, secondContext.<Long>resolve(ONE, null).longValue());
    }


    private static class ContextDependentTest implements Runnable {

        private final ApplicationContext rootContext;

        private final String contextName;


        public ContextDependentTest(ApplicationContext rootContext, String contextName) {
            this.rootContext = rootContext;
            this.contextName = contextName;
        }

        @Override
        public void run() {
            final Function<Object[], Object> contextCreation = (objects) -> new ChildContextCreation(
                    rootContext, contextName);
            rootContext.<Command>resolve(
                    REGISTRATION_COMMAND, new Object[]{ contextName + CHILD_CONTEXT_CREATION, contextCreation})
                    .execute();
            rootContext.<Command>resolve(contextName + CHILD_CONTEXT_CREATION, null).execute();
            final Function<Object[], Object> contextSelection = (objects) ->
                    new ContextSelection(rootContext).getChild((String) objects[0]);
            rootContext.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ CONTEXT_SELECTION, contextSelection})
                    .execute();
            final ApplicationContext context = rootContext.resolve(CONTEXT_SELECTION, new Object[]{ contextName });
            final Function<Object[], Object> function = (objects) -> Long.valueOf(1);
            context.<Command>resolve(REGISTRATION_COMMAND, new Object[]{ ONE, function }).execute();
        }
    }
}