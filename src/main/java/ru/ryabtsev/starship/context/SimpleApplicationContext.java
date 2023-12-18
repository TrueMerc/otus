package ru.ryabtsev.starship.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import ru.ryabtsev.starship.actions.context.DependencyRegistration;

public class SimpleApplicationContext implements ApplicationContext {

    private static final int DEPENDENCIES_INITIAL_CAPACITY = 128;

    private final ApplicationContext parent;

    private final Map<String, Function<Object[], Object>> dependencies;

    private final List<ApplicationContext> children;

    private final String name;

    public SimpleApplicationContext(final ApplicationContext parent, final String name) {
        this.parent = parent;
        this.dependencies = new ConcurrentHashMap<>(DEPENDENCIES_INITIAL_CAPACITY);
        dependencies.put(
                DependencyRegistration.class.getSimpleName(),
                (objects) -> new DependencyRegistration(
                        this, (String) objects[0], (Function<Object[], Object>) objects[1]
                )
        );
        this.children = Collections.synchronizedList(new ArrayList<>());
        this.name = name;
    }

    public SimpleApplicationContext() {
        this(null, "root");
    }

    @Override
    public <T> T resolve(final String key, final Object[] parameters) {
        final Function<Object[], Object> dependency = Optional.ofNullable(dependencies.get(key))
                .orElseGet(() -> this.findParentDependency(key));
        return (T) dependency.apply(parameters);
    }

    private Function<Object[], Object> findParentDependency(final String key) {
        if (parent == null) {
            return null;
        }
        if (parent.resolve(key, null) != null) {
            return parent.resolve(key, null);
        } else {
            if (parent instanceof SimpleApplicationContext typedParent) {
                return typedParent.findParentDependency(key);
            } else {
                throw new IllegalStateException("Can't process parent of type " + parent.getClass().getName());
            }
        }
    }

    public Map<String, Function<Object[], Object>> getDependencies() {
        return dependencies;
    }

    public List<ApplicationContext> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }
}
