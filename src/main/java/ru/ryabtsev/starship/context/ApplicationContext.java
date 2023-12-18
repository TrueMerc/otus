package ru.ryabtsev.starship.context;

/**
 * Базовый интерфейс для IoC-контейнеров, используемых в приложении.
 */
public interface ApplicationContext {

    /**
     * Возвращает объект по заданному ключу и параметрам.
     * @param key ключ, которому соответствует объект.
     * @param parameters параметры.
     * @return объект, заданный ключом и соответствующими параметрами.
     * @param <T> тип возвращаемого объекта.
     */
    <T> T resolve(String key, Object[] parameters);
}
