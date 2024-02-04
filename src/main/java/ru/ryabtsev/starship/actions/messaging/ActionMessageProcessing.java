package ru.ryabtsev.starship.actions.messaging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.movement.Movement;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.network.messages.ActionMessage;

public class ActionMessageProcessing implements Command {

    private static final String WRONG_CLASS = "Class %s doesn't inherit the Command class";

    private final ApplicationContext applicationContext;

    private final ActionMessage actionMessage;

    public ActionMessageProcessing(final ApplicationContext applicationContext, final ActionMessage actionMessage) {
        this.applicationContext = applicationContext;
        this.actionMessage = actionMessage;
    }

    @Override
    public void execute() {
        final var commandQueue = (CommandQueue) applicationContext.resolve("MessageQueue", null);
        final var object = applicationContext.resolve(actionMessage.getObjectId(), null);
        final var apiMap = (Map<String, String>) applicationContext.resolve("ApiMap", null);
        try {
            final Class<?> actionClass = Class.forName(apiMap.get(actionMessage.getAction()));
            if (!Command.class.isAssignableFrom(actionClass)) {
                throw new IllegalArgumentException(String.format(WRONG_CLASS, actionClass.getName()));
            }
            final List<Object> parameters = actionMessage.getParameters();
            final Object[] constructorArguments = Stream.concat(Stream.of(object), parameters.stream())
                    .toArray(Object[]::new);
            final Class<?>[] argumentTypes = Arrays.stream(constructorArguments)
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new);
            final Constructor<?> actionConstructor = Arrays.stream(actionClass.getConstructors())
                    .filter(constructor -> constructorMatchesArgumentTypes(constructor, argumentTypes))
                    .findFirst()
                    .orElseThrow(NoSuchMethodException::new);
            final Object commandObject = actionConstructor.newInstance(constructorArguments);
            if (!(commandObject instanceof Command)) {
                throw new IllegalStateException("Object that has been created isn't the Command object");
            }
            commandQueue.add((Command) commandObject);
        } catch (final ClassNotFoundException
                       | InvocationTargetException
                       | InstantiationException
                       | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    boolean constructorMatchesArgumentTypes(Constructor<?> constructor, Class<?>[] argumentTypes) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final int length = parameterTypes.length;
        if (length == argumentTypes.length) {
            for (int number = 0; number < length; ++number) {
                if (!parameterTypes[number].isAssignableFrom(argumentTypes[number])) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
