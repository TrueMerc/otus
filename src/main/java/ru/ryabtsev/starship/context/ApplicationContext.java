package ru.ryabtsev.starship.context;

/**
 * Basic interface for IoC-containers that are used in this application.
 */
public interface ApplicationContext {

    /**
     * Return dependency for given key ond parameters.
     * @param key key for dependency identification.
     * @param parameters parameters.
     * @return dependency for given key ond parameters.
     * @param <T> type of returned value.
     */
    <T> T resolve(String key, Object... parameters);
}
