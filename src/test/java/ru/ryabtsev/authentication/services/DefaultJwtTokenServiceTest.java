package ru.ryabtsev.authentication.services;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class DefaultJwtTokenServiceTest {

    private final UserService userService = new TestUserService();

    private final JwtTokenService jwtTokenService = new DefaultJwtTokenService(userService);


    @Test
    void tokenGenerationTest() {

        final String userName = "user";

        final String header = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";

        final String payload = """
                eyJpc3MiOiJBdXRoZW50aWNhdGlvbiBTZXJ2aWNlIiwic3ViIjoi\
                YXV0aGVudGljYXRpb24iLCJhdWQiOiJ1c2VycyIsInVzZXJJZCI6IjEifQ""";

        final String signature = "nIiGhZyYwpI6njZnWppHUjcKJ8BvNlslZOAX13OkWRo";

        final String token = jwtTokenService.createTokenFor(userName);

        assertNotNull(token);
        final String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
        final String decodedHeader = new String(Base64.getDecoder().decode(tokenParts[0]), StandardCharsets.UTF_8);
        assertEquals(header, tokenParts[0]);
        final String decodedPayload = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);
        assertEquals(payload, tokenParts[1]);
        assertEquals(signature, tokenParts[2]);
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