package ru.ryabtsev.starship.context;

import java.util.List;

public class ContextSelection {

    private SimpleApplicationContext applicationContext;

    public ContextSelection(ApplicationContext applicationContext) {
        if (applicationContext instanceof SimpleApplicationContext simpleApplicationContext) {
            this.applicationContext = simpleApplicationContext;
        }
    }

    public ApplicationContext getChild(final String name) {
        final List<ApplicationContext> children = applicationContext.getChildren();
        if (children.isEmpty()) {
            return null;
        }
        for (ApplicationContext child : children) {
            if (child instanceof SimpleApplicationContext typedChild) {
                if (name.equals(typedChild.getName())) {
                    return child;
                } else {
                    return new ContextSelection(child).getChild(name);
                }
            }
        }
        return null;
    }
}
