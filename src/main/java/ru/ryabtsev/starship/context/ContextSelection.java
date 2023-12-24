package ru.ryabtsev.starship.context;

import java.util.List;

/**
 * Dependency that allows to select context by its name.
 */
public class ContextSelection {

    private SimpleApplicationContext applicationContext;

    public ContextSelection(ApplicationContext applicationContext) {
        if (applicationContext instanceof SimpleApplicationContext simpleApplicationContext) {
            this.applicationContext = simpleApplicationContext;
        }
    }

    public ApplicationContext getChild(final String name) {
        if (applicationContext.getName().equals(name)) {
            return applicationContext;
        }
        final List<ApplicationContext> children = applicationContext.getChildren();
        if (children.isEmpty()) {
            return null;
        }
        for (ApplicationContext child : children) {
            if (child instanceof SimpleApplicationContext typedChild) {
                if (name.equals(typedChild.getName())) {
                    return child;
                }
            }
        }
        for (ApplicationContext child : children) {
            final ApplicationContext internalChild = new ContextSelection(child).getChild(name);
            if (internalChild != null) {
                return internalChild;
            }
        }
        return null;
    }
}
