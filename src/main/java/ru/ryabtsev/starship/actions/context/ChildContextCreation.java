package ru.ryabtsev.starship.actions.context;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.context.SimpleApplicationContext;

/**
 * The class that implements command for a creation of new context.
 */
public class ChildContextCreation implements Command {

    private final ApplicationContext parentContext;

    private final String name;

    /**
     * Creates new instance of the command that add a new child context to given parent context.
     * @param parentContext parent context.
     * @param name name of a new context.
     */
    public ChildContextCreation(final ApplicationContext parentContext, final String name) {
        this.parentContext = parentContext;
        this.name = name;
    }

    @Override
    public void execute() {
        if (parentContext instanceof SimpleApplicationContext simpleApplicationContext) {
            simpleApplicationContext.getChildren().add(new SimpleApplicationContext(parentContext, name));
        }
    }
}
