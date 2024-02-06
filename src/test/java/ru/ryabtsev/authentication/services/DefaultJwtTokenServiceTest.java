package ru.ryabtsev.authentication.services;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.authentication.entities.User;

class DefaultJwtTokenServiceTest {

    private final UserService userService = new TestUserService();

    private final GameService gameService = new SimpleGameService();

    private final JwtTokenService jwtTokenService = new DefaultJwtTokenService(userService, gameService);


    @Test
    void tokenGenerationTest() {

//        final String userName = "user";
//
//        final String header = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";
//
//        final String payload = """
//                eyJpc3MiOiJBdXRoZW50aWNhdGlvbiBTZXJ2aWNlIiwic3ViIjoi\
//                YXV0aGVudGljYXRpb24iLCJhdWQiOiJ1c2VycyIsInVzZXJJZCI6IjEifQ""";
//
//        final String signature = "nIiGhZyYwpI6njZnWppHUjcKJ8BvNlslZOAX13OkWRo";
//
//        final String token = jwtTokenService.createTokenFor(new User(UUID.randomUUID(), userName, "123"));
    }

    private static class TestUserService implements UserService {
        @Override
        public boolean validatesCredentials(String userName, String password) {
            return false;
        }

        @Override
        public Long getIdByUserName(String userName) {
            return 1L;
        }
    }
}