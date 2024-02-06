package ru.ryabtsev.authentication.services;

import java.util.List;
import java.util.Optional;
import ru.ryabtsev.authentication.entities.Game;
import ru.ryabtsev.authentication.entities.User;
import ru.ryabtsev.authentication.services.messages.GameMessage;

public interface GameService {

    Game createGame(List<User> users);

    Optional<Game> getGame(User user);
}
