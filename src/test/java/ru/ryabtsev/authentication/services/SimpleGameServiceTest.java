package ru.ryabtsev.authentication.services;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.authentication.entities.Game;
import ru.ryabtsev.authentication.entities.User;
import ru.ryabtsev.authentication.services.messages.input.NewGameRequest;
import ru.ryabtsev.authentication.services.messages.output.GameMessage;

class SimpleGameServiceTest {

    private final GameService gameService = new SimpleGameService();

    private final UserService userService = new SimpleUserService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void gameCreationTest() {
        // Arrange:
        userService.addAll(List.of(
                new User(UUID.fromString("00000000-0000-0000-0000-000000000001"), "one", "1"),
                new User(UUID.fromString("00000000-0000-0000-0000-000000000002"), "two", "2"),
                new User(UUID.fromString("00000000-0000-0000-0000-000000000003"), "three", "3")
        ));

        // language=JSON
        final String requestString = """
                {
                    "logins": [ "one", "two", "three"] 
                }    
                """;

        // Act:
        final NewGameRequest newGameRequest = objectMapper.readValue(requestString, NewGameRequest.class);
        final List<User> users = userService.getAllByNames(newGameRequest.logins());
        final Game game = gameService.createGame(users);
        final String resultMessage = objectMapper.writeValueAsString(new GameMessage(game.id()));

        // Assert:
        assertEquals(3, newGameRequest.logins().size());
        assertEquals(3, users.size());
        assertTrue(resultMessage.contains(game.id().toString()));
    }
}