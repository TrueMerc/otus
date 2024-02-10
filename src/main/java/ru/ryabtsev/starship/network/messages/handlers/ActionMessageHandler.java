package ru.ryabtsev.starship.network.messages.handlers;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import ru.ryabtsev.security.jwt.JsonWebToken;
import ru.ryabtsev.starship.actions.messaging.ActionMessageProcessing;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.network.messages.ActionMessage;
import ru.ryabtsev.starship.security.ActionAvailabilityCheck;

public class ActionMessageHandler implements HttpHandler {

    private static final String SECRET_KEY = "29d17f180f9dfc62bc7fd7fed48a61b5c5931de47a7f0def962c5ce91063a83c";

    private static final String OK_MESSAGE = "OK";

    private static final String BAD_REQUEST_MESSAGE = "Bad Request";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final int OK_CODE = 200;

    private static final int BAD_REQUEST_CODE = 400;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CommandQueue commandQueue;

    private final ApplicationContext applicationContext;

    public ActionMessageHandler(final CommandQueue commandQueue, final ApplicationContext applicationContext) {
        this.commandQueue = commandQueue;
        this.applicationContext = applicationContext;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final var requestBody = exchange.getRequestBody();
        final var authorizationHeader = exchange.getRequestHeaders().getFirst(AUTHORIZATION_HEADER);
        final var tokenString = authorizationHeader.split("\\s")[1];
        boolean success = true;
        try {
            final var actionMessage = OBJECT_MAPPER.readValue(requestBody, ActionMessage.class);
            final var token = new JsonWebToken(tokenString);
            final var check = new ActionAvailabilityCheck(token, actionMessage.getGameId(), SECRET_KEY);
            if (check.isSuccessful()) {
                commandQueue.add(new ActionMessageProcessing(applicationContext, actionMessage));
            }
        } catch (final StreamReadException | DatabindException e) {
            success = false;
        }

        final String response = success ? OK_MESSAGE : BAD_REQUEST_MESSAGE;
        final int responseCode = success ? OK_CODE : BAD_REQUEST_CODE;

        try {
            exchange.sendResponseHeaders(responseCode, response.length());
            final var responseBody = exchange.getResponseBody();
            responseBody.write(response.getBytes(StandardCharsets.UTF_8));
            responseBody.close();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
