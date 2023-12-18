package ru.ryabtsev.starship.actions.context;

import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.context.SimpleApplicationContext;

public class ChildContextCreation implements Command {

    private final ApplicationContext parentContext;

    private final String name;

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
