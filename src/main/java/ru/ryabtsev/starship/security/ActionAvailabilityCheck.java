package ru.ryabtsev.starship.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.SneakyThrows;
import ru.ryabtsev.security.jwt.JsonWebToken;
import ru.ryabtsev.starship.network.messages.ActionMessage;

public class ActionAvailabilityCheck {

    private final JsonWebToken jsonWebToken;

    private final String gameId;

    private final String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActionAvailabilityCheck(final JsonWebToken jsonWebToken, final String gameId, final String secretKey) {
        this.jsonWebToken = jsonWebToken;
        this.gameId = gameId;
        this.secretKey = secretKey;
    }

    public boolean isSuccessful() {
        return jsonWebToken.isValidFor(secretKey) && gameId.equals(getGameIdFromToken(jsonWebToken));
    }

    @SneakyThrows
    private  String getGameIdFromToken(final JsonWebToken jsonWebToken) {
        final String payload = jsonWebToken.getEncodedPayload();
        final String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
        return objectMapper.readTree(decodedPayload).findValue("gameId").asText();
    }
}
