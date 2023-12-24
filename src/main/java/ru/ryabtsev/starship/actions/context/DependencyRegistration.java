package ru.ryabtsev.starship.actions.context;

import java.util.function.Function;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.context.SimpleApplicationContext;

/**
 * The class that allows to register a new dependency in a context.
 */
public class DependencyRegistration implements Command {

    private final ApplicationContext applicationContext;

    private final String key;

    private final Function<Object[], Object> dependency;

    /**
     * Creates new instance of the command that add a new command to a given context.
     * @param applicationContext current context.
     * @param key key that allows to get dependency from the current context.
     * @param dependency dependency.
     */
    public DependencyRegistration(
            final ApplicationContext applicationContext,
            final String key,
            final Function<Object[], Object> dependency) {
        this.applicationContext = applicationContext;
        this.key = key;
        this.dependency = dependency;
    }

    @Override
    public void execute() {
        if (applicationContext instanceof SimpleApplicationContext simpleApplicationContext) {
            simpleApplicationContext.getDependencies().put(key, dependency);
        } else {
            throw new IllegalArgumentException(
                    "Wrong type of context for this command: " + applicationContext.getClass().getName());
        }
    }
}
