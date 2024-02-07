package ru.ryabtsev.authentication.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class JsonWebTokenTest {

    private static final String SECRET_KEY = "29d17f180f9dfc62bc7fd7fed48a61b5c5931de47a7f0def962c5ce91063a83c";

    // language=JSON
    private static final String HEADER = """
            {
                "typ": "JWT",
                "alg": "HS256"
            }
            """;

    // language=JSON
    private static final String PAYLOAD = """
            {
                "iss": "Authentication Service",
                "sub": "authentication",
                "aud": "users",
                "userId": "1"
            }
            """;

    @Test
    void tokenGenerationTest() {
        final String header = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";

        final String payload = """
                eyJpc3MiOiJBdXRoZW50aWNhdGlvbiBTZXJ2aWNlIiwic3ViIjoi\
                YXV0aGVudGljYXRpb24iLCJhdWQiOiJ1c2VycyIsInVzZXJJZCI6IjEifQ""";

        final String signature = "nIiGhZyYwpI6njZnWppHUjcKJ8BvNlslZOAX13OkWRo";

        final String token = new JsonWebToken(HEADER, PAYLOAD, SECRET_KEY).toString();

        assertNotNull(token);
        final String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
        final String decodedHeader = new String(Base64.getDecoder().decode(tokenParts[0]), StandardCharsets.UTF_8);
        assertEquals(header, tokenParts[0]);
        final String decodedPayload = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);
        assertEquals(payload, tokenParts[1]);
        assertEquals(signature, tokenParts[2]);
    }

    @Test
    void tokenVerificationTest() {
        // Arrange:
        final String header = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";

        final String payload = """
                eyJpc3MiOiJBdXRoZW50aWNhdGlvbiBTZXJ2aWNlIiwic3ViIjoi\
                YXV0aGVudGljYXRpb24iLCJhdWQiOiJ1c2VycyIsInVzZXJJZCI6IjEifQ""";

        final String signature = "nIiGhZyYwpI6njZnWppHUjcKJ8BvNlslZOAX13OkWRo";

        final String wrongKey = "29d17f180f9dfc62bc7fd7fed48a61b5c5931de47a7f0def962c5ce91063a83b";

        // Act:
        final JsonWebToken token = new JsonWebToken(String.join(".", header, payload, signature));

        // Assert:
        assertTrue(token.isValidFor(SECRET_KEY));
        assertFalse(token.isValidFor(wrongKey));
    }
}