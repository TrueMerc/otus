package ru.ryabtsev.authentication.services;

import java.util.UUID;
import ru.ryabtsev.authentication.entities.Game;
import ru.ryabtsev.authentication.entities.User;
import ru.ryabtsev.security.jwt.JsonWebToken;

public class DefaultJwtTokenService implements JwtTokenService {

    private static final String SECRET_KEY = "29d17f180f9dfc62bc7fd7fed48a61b5c5931de47a7f0def962c5ce91063a83c";

    // language=JSON
    private static final String HEADER = """
            {
                "typ": "JWT",
                "alg": "HS256"
            }
            """;

    // language=JSON
    private static final String PAYLOAD_TEMPLATE = """
            {
                "iss": "Authentication Service",
                "sub": "authentication",
                "aud": "users",
                "userId": "%s",
                "gameId": "%s"
            }
            """;

    private final UserService userService;

    private final GameService gameService;

    public DefaultJwtTokenService(final UserService userService, final GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public String createTokenFor(final User user) {
        final Game game = gameService.getGame(user)
                .orElseThrow(() -> new IllegalArgumentException("Can't find user " + user));
        final String userName = user.login();
        return new JsonWebToken(HEADER, formPayload(userName, game.id()), SECRET_KEY).toString();
    }

    private String formPayload(final String userName, final UUID gameId) {
        final String userId = userService.getByUserName(userName)
                .map(User::id)
                .map(UUID::toString)
                .orElseThrow(() -> new IllegalArgumentException("User with name " + userName + "doesn't exist"));
        return String.format(PAYLOAD_TEMPLATE, userId, gameId.toString());
    }
}
