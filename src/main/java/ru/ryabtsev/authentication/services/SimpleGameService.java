package ru.ryabtsev.authentication.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import ru.ryabtsev.authentication.entities.Game;
import ru.ryabtsev.authentication.entities.User;

/**
 * Provides a simple implementation of GameService which should be used for testing purposes only.
 */
public class SimpleGameService implements GameService {

    private static final int DEFAULT_CAPACITY = 32;

    private final Map<UUID, Game> games;

    public SimpleGameService() {
        games = new HashMap<>(DEFAULT_CAPACITY);
    }

    @Override
    public Game createGame(final List<User> users) {
        final UUID gameId = UUID.randomUUID();
        final Game game = new Game(gameId, users);
        games.put(gameId, game);
        return game;
    }

    @Override
    public Optional<Game> getGame(final User user) {
        return games.entrySet()
                .stream()
                .filter(gameEntry -> gameEntry.getValue().contains(user))
                .findFirst()
                .map(Map.Entry::getValue);
    }
}
