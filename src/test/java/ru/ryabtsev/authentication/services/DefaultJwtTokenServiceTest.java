package ru.ryabtsev.authentication.services;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.authentication.entities.Game;
import ru.ryabtsev.authentication.entities.User;

class DefaultJwtTokenServiceTest {

    private final List<User> users = List.of(
            new User(UUID.fromString("00000000-0000-0000-0000-000000000001"), "one", "1"),
            new User(UUID.fromString("00000000-0000-0000-0000-000000000002"), "two", "2"),
            new User(UUID.fromString("00000000-0000-0000-0000-000000000003"), "three", "3"));

    private final UserService userService = new SimpleUserService();

    private final GameService gameService = new SimpleGameService();

    private final JwtTokenService jwtTokenService = new DefaultJwtTokenService(userService, gameService);

    @BeforeEach
    void setUp() {
        userService.addAll(users);
    }


    @Test
    void tokenGenerationTest() {
        // Act:
        final Game game = gameService.createGame(users);
        final User firstUser = users.get(0);
        final String token = jwtTokenService.createTokenFor(firstUser);
        final String[] tokenParts = token.split("\\.");
        final String decodedPayload = new String(Base64.getUrlDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);

        // Assert:
        assertEquals(3, tokenParts.length);
        assertTrue(decodedPayload.contains(firstUser.id().toString()));
        assertTrue(decodedPayload.contains(game.id().toString()));
    }
}