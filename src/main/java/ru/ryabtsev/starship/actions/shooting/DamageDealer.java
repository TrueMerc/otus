package ru.ryabtsev.starship.actions.shooting;

/**
 * The interface for all game objects that can make damage.
 */
public interface DamageDealer {


    /**
     * Returns a rate of damage that this damage dealer makes.
     * @return rate of damage that this damage dealer makes.
     */
    int damageRate();
}
